# Define all hardcoded local variable and local variables looked up from data resources
locals {
  stack_name                = "identity" # this must match the stack name the service deploys into
  name_prefix               = "${local.stack_name}-${var.environment}"
  service_name              = "acsp-api"
  container_port            = "8080" # default Java port to match start script
  docker_repo               = "acsp-api"
  lb_listener_rule_priority = 14
  lb_listener_paths         = ["acsp/*"]
  healthcheck_path          = "/healthcheck" #healthcheck path for acsp-api service
  healthcheck_matcher       = "200"

  kms_alias                 = "alias/${var.aws_profile}/environment-services-kms"
  service_secrets           = jsondecode(data.vault_generic_secret.service_secrets.data_json)

  parameter_store_secrets    = {
    "vpc_name"                        = local.vpc_name
  }

  vpc_name                         = local.service_secrets["vpc_name"]

  # create a map of secret name => secret arn to pass into ecs service module
  # using the trimprefix function to remove the prefixed path from the secret name
  secrets_arn_map = {
    for sec in data.aws_ssm_parameter.secret:
      trimprefix(sec.name, "/${local.name_prefix}/") => sec.arn
  }

  service_secrets_arn_map = {
    for sec in module.secrets.secrets:
      trimprefix(sec.name, "/${local.service_name}-${var.environment}/") => sec.arn
  }

  # TODO: task_secrets don't seem to correspond with 'parameter_store_secrets'. What is the difference?
  task_secrets = [
  ]

  task_environment = [
    { "name": "LOG_LEVEL", "value": "${var.log_level}" },
  ]
}
