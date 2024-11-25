resource "aws_instance" "app" {
  ami           = "ami-04dd23e62ed049936"
  instance_type = "t3.micro"

  tags = {
    Name = "AppServer"
    tf2puml.as = "app"
    technology = "SpringBoot"
  }
}

resource "aws_s3_bucket" "contents" {
  bucket = "com.t-horie.terra-plantuml.contents"

  tags = {
    Name = "ContentsBucket"
    tf2puml.as = "s3"
  }
}