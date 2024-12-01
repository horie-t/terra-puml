resource "aws_instance" "example" {
  ami           = "ami-04dd23e62ed049936"
  instance_type = "t3.micro"

    tags = {
      Name = "WebServer"
      "tf2puml.as" = "web"
    }
}