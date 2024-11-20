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

/*
 * Network
 */
resource "aws_vpc" "terrapuml" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "TerraPUML_VPC"
    asPlantUML = "vpc"
  }
}

resource "aws_internet_gateway" "terrapuml" {
  vpc_id = aws_vpc.terrapuml.id

  tags = {
    Name = "InternetGateway"
    asPlantUML = "i_gateway"
  }
}

// Public Subnet
resource "aws_subnet" "public_a" {
  vpc_id = aws_vpc.terrapuml.id

  cidr_block = "10.0.0.0/24"
  availability_zone = "us-west-2a"
  map_public_ip_on_launch = true

  tags = {
    Name = "PublicSubnetA"
    asPlantUML = "subnet_pub_a"
  }
}

resource "aws_subnet" "public_c" {
  vpc_id = aws_vpc.terrapuml.id

  cidr_block = "10.0.1.0/24"
  availability_zone = "us-west-2c"
  map_public_ip_on_launch = true

  tags = {
    Name = "PublicSubnetC"
    asPlantUML = "subnet_pub_c"
  }
}

resource "aws_eip" "nat_gateway_a" {
  depends_on = [aws_internet_gateway.terrapuml]
}

resource "aws_eip" "nat_gateway_c" {
  depends_on = [aws_internet_gateway.terrapuml]
}

resource "aws_nat_gateway" "terrapuml_a" {
  allocation_id = aws_eip.nat_gateway_a.id
  subnet_id = aws_subnet.public_a.id

  depends_on = [aws_internet_gateway.terrapuml]

  tags = {
    Name = "NatGatewayA"
    asPlantUML = "nat_gateway_a"
  }
}

resource "aws_nat_gateway" "terrapuml_c" {
  allocation_id = aws_eip.nat_gateway_c.id
  subnet_id = aws_subnet.public_c.id

  depends_on = [aws_internet_gateway.terrapuml]

  tags = {
    Name = "NatGatewayC"
    asPlantUML = "nat_gateway_c"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.terrapuml.id
}

resource "aws_route" "public_route" {
  route_table_id = aws_route_table.public.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.terrapuml.id
}

resource "aws_route_table_association" "public_a" {
  subnet_id = aws_subnet.public_a.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_c" {
  subnet_id = aws_subnet.public_c.id
  route_table_id = aws_route_table.public.id
}

// Private Subnet
resource "aws_subnet" "private_a" {
  vpc_id = aws_vpc.terrapuml.id

  cidr_block = "10.0.128.0/24"
  availability_zone = "us-west-2a"
  map_public_ip_on_launch = false

  tags = {
    Name = "PrivateSubnetA"
    asPlantUML = "subnet_pri_a"
  }
}

resource "aws_subnet" "private_c" {
  vpc_id = aws_vpc.terrapuml.id

  cidr_block = "10.0.129.0/24"
  availability_zone = "us-west-2c"
  map_public_ip_on_launch = false

  tags = {
    Name = "PrivateSubnetC"
    asPlantUML = "subnet_pri_c"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.terrapuml.id
}

resource "aws_route" "private" {
  route_table_id = aws_route_table.private.id
  nat_gateway_id = aws_nat_gateway.terrapuml_a.id
  destination_cidr_block = "0.0.0.0/0"
}

resource "aws_route_table_association" "private_a" {
  subnet_id = aws_subnet.private_a.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_c" {
  subnet_id = aws_subnet.private_c.id
  route_table_id = aws_route_table.private.id
}

// Security Group
resource "aws_security_group" "terrapuml" {
  vpc_id = aws_vpc.terrapuml.id
}

resource "aws_security_group_rule" "ingress_http" {
  from_port         = 80
  to_port           = 80
  type              = "ingress"
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.terrapuml.id
}

resource "aws_security_group_rule" "egress_any" {
  from_port         = 0
  to_port           = 0
  type              = "egress"
  protocol          = -1
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.terrapuml.id
}

resource "aws_security_group_rule" "ingress_https" {
  from_port         = 443
  to_port           = 443
  type              = "ingress"
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.terrapuml.id
}

resource "aws_security_group_rule" "http_redirect" {
  from_port         = 8080
  to_port           = 8080
  type              = "ingress"
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.terrapuml.id
}

// Compute
resource "aws_lb" "frontend" {
  name               = "frontend"
  internal           = false
  load_balancer_type = "application"
  idle_timeout       = 60
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_c.id]
  security_groups    = [aws_security_group.terrapuml.id]

  access_logs {
    bucket = aws_s3_bucket.alb_log.id
    enabled = true
  }

  tags = {
    Name = "FrontendLoadBalancer"
    asPlantUML = "alb"
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.frontend.arn
  port              = 80
  protocol = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "This is HTTP"
      status_code = "200"
    }
  }
}

// Storage
data "aws_caller_identity" "current" {}
data "aws_elb_service_account" "main" {}

resource "aws_s3_bucket" "alb_log" {
  bucket = "access-logsterra-plantuml.t-horie.com"

  tags = {
    Name = "AccessLogsBucket"
    asPlantUML = "s3"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "alb_log" {
  bucket = aws_s3_bucket.alb_log.id

  rule {
    id     = "expire-logs"
    status = "Enabled"
    expiration {
      days = 180
    }
  }
}

resource "aws_s3_bucket_policy" "alb_log" {
  bucket = aws_s3_bucket.alb_log.id
  policy = data.aws_iam_policy_document.alb_log.json
}

data "aws_iam_policy_document" "alb_log" {
  statement {
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [data.aws_elb_service_account.main.arn]
    }
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.alb_log.arn}/AWSLogs/${data.aws_caller_identity.current.account_id}/*"]
  }
}

// Output
output "frontend_alb_dns_name" {
  value = aws_lb.frontend.dns_name
}