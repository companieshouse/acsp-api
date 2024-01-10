# Define all hardcoded local variable and local variables looked up from data resources
locals {
  stack_name                = "identity" # this must match the stack name the service deploys into
  name_prefix               = "${local.stack_name}-${var.environment}"
  global_prefix             = "global-${var.environment}"
  service_name              = "acsp-api"
  container_port            = "18642" # default Java port to match start script
  docker_repo               = "acsp-api"
  lb_listener_rule_priority = 14
  lb_listener_paths         = ["/acsp/*"]
  healthcheck_path          = "/healthcheck" #healthcheck path for acsp-api service
  healthcheck_matcher       = "200"
  application_subnet_ids    = data.aws_subnets.application.ids
  kms_alias                 = "alias/${var.aws_profile}/environment-services-kms"
  service_secrets           = jsondecode(data.vault_generic_secret.service_secrets.data_json)
  stack_secrets             = jsondecode(data.vault_generic_secret.stack_secrets.data_json)
  application_subnet_pattern = local.stack_secrets["application_subnet_pattern"]
  use_set_environment_files  = var.use_set_environment_files
  s3_config_bucket           = data.vault_generic_secret.shared_s3.data["config_bucket_name"]
  app_environment_filename   = "acsp-api.env"
  vpc_name                   = data.aws_ssm_parameter.secret[format("/%s/%s", local.name_prefix, "vpc-name")].value

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

  global_secret_list = flatten([for key, value in local.global_secrets_arn_map :
    { "name" = upper(key), "valueFrom" = value }
  ])

  global_secrets_arn_map = {
    for sec in data.aws_ssm_parameter.global_secret :
    trimprefix(sec.name, "/${local.global_prefix}/") => sec.arn
  }

  service_secret_list = flatten([for key, value in local.service_secrets_arn_map :
    { "name" = upper(key), "valueFrom" = value }
  ])

  ssm_service_version_map = [
    for sec in module.secrets.secrets : {
      name  = "${replace(upper(local.service_name), "-", "_")}_${var.ssm_version_prefix}${replace(upper(basename(sec.name)), "-", "_")}",
      value = tostring(sec.version)
    }
  ]

  ssm_global_version_map = [
    for sec in data.aws_ssm_parameter.global_secret : {
      name  = "GLOBAL_${var.ssm_version_prefix}${replace(upper(basename(sec.name)), "-", "_")}",
      value = tostring(sec.version)
    }
  ]

  # secrets to go in list
#  task_secrets = concat(local.global_secret_list, local.service_secret_list, [
#    { "name" : "COOKIE_SECRET", "valueFrom" : "${local.service_secrets_arn_map.cookie_secret}" },
#    { "name" : "CHS_DEVELOPER_CLIENT_SECRET", "valueFrom" : "${local.service_secrets_arn_map.chs_developer_client_secret}" },
#    { "name" : "CHS_DEVELOPER_CLIENT_ID", "valueFrom" : "${local.service_secrets_arn_map.chs_developer_client_id}" },
#    { "name" : "OAUTH2_REQUEST_KEY", "valueFrom" : "${local.service_secrets_arn_map.oauth2_request_key}" },
#    { "name" : "DEVELOPER_OAUTH2_REQUEST_KEY", "valueFrom" : "${local.service_secrets_arn_map.developer_oauth2_request_key}" },
#  ])

  task_secrets = concat(local.global_secret_list, local.service_secret_list)

  task_environment = concat(local.ssm_global_version_map,local.ssm_service_version_map)
}
