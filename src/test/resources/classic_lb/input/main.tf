terraform {
  required_version = ">= 1.0.0" # Ensure that the Terraform version is 1.0.0 or higher

  required_providers {
    aws = {
      source = "hashicorp/aws" # Specify the source of the AWS provider
      version = "~> 4.0"        # Use a version of the AWS provider that is compatible with version
    }
  }
}

provider "aws" {
  region = "us-west-2"
}

resource "aws_vpc" "example_vpc" {
  cidr_block           = "10.0.0.0/16"
  # enable_dns_support   = true
  # enable_dns_hostnames = true
  tags = {
    Name = "example-vpc"
    "tf2puml:as" = "vpc"
  }
}

resource "aws_subnet" "public_subnet" {
  vpc_id                  = aws_vpc.example_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "us-west-2a"
  map_public_ip_on_launch = true
  tags = {
    Name = "public-subnet"
    "tf2puml:as" = "subnet_pub_a"
    "tf2puml:parent" = "vpc"
  }
}

resource "aws_subnet" "private_subnet" {
  vpc_id            = aws_vpc.example_vpc.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "us-west-2a"
  tags = {
    Name = "private-subnet"
    "tf2puml:as" = "subnet_pri_a"
    "tf2puml:parent" = "vpc"
  }
}

resource "aws_internet_gateway" "example_igw" {
  vpc_id = aws_vpc.example_vpc.id
  tags = {
    Name = "example-igw"
    "tf2puml:as" = "i_gateway"
    "tf2puml:parent" = "vpc"
  }
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.example_vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.example_igw.id
  }
  tags = {
    Name = "public-route-table"
  }
}

resource "aws_route_table_association" "public_subnet_assoc" {
  subnet_id      = aws_subnet.public_subnet.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_eip" "nat_eip" {
  depends_on = [aws_internet_gateway.example_igw]
  tags = {
    Name = "nat-eip"
  }
}
resource "aws_nat_gateway" "example_nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_subnet.id
  depends_on = [aws_internet_gateway.example_igw]
  tags = {
    Name = "example-nat"
    "tf2puml:as" = "nat_gateway_a"
    "tf2puml:parent" = "subnet_pub_a"
  }
}

resource "aws_route_table" "private_route_table" {
  vpc_id = aws_vpc.example_vpc.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.example_nat.id
  }
  tags = {
    Name = "private-route-table"
  }
}

resource "aws_route_table_association" "private_subnet_assoc" {
  subnet_id      = aws_subnet.private_subnet.id
  route_table_id = aws_route_table.private_route_table.id
}

resource "aws_security_group" "ec2_sg" {
  vpc_id = aws_vpc.example_vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    security_groups = [aws_security_group.elb_sg.id]  # ELBからのHTTPトラフィックを許可
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "ec2-sg"
  }
}

resource "aws_security_group" "elb_sg" {
  vpc_id = aws_vpc.example_vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # 全世界からのHTTPトラフィックを許可
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "elb-sg"
  }
}

resource "aws_instance" "private_instance" {
  ami                    = "ami-061dd8b45bc7deb3d"  # Amazon Linux 2のAMI ID（リージョンによって異なる）
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.private_subnet.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  key_name               = "tetsuya-key-pair-oregon"  # 事前に作成したキーペアの名前

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              amazon-linux-extras install nginx1 -y
              systemctl start nginx
              systemctl enable nginx
              EOF

  tags = {
    Name = "private-instance"
    "tf2puml:as" = "ec2"
    "tf2puml:parent" = "subnet_pri_a"
  }
}

resource "aws_elb" "example_elb" {
  name               = "example-elb"
  subnets            = [aws_subnet.public_subnet.id]
  security_groups    = [aws_security_group.elb_sg.id]

  listener {
    instance_port     = 80
    instance_protocol = "HTTP"
    lb_port           = 80
    lb_protocol       = "HTTP"
  }

  health_check {
    target              = "HTTP:80/"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }

  instances = [aws_instance.private_instance.id]

  tags = {
    Name = "example-elb"
    "tf2puml:as" = "elb"
    "tf2puml:parent" = "vpc"
  }
}

output "elb_dns_name" {
  value = aws_elb.example_elb.dns_name
}
