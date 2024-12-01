// Compute
resource "aws_lb" "frontend" {
  name               = "frontend"
  internal           = false
  load_balancer_type = "application"
  idle_timeout       = 60
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_c.id]
  security_groups    = [aws_security_group.tf2puml.id]

  access_logs {
    bucket = aws_s3_bucket.alb_log.id
    enabled = true
  }

  tags = {
    Name = "FrontendLoadBalancer"
    "tf2puml.as" = "alb"
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
