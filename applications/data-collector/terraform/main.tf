variable "project_id"       {}
variable "region"           {}
variable "app_name"         {}
variable "db_instance_name" {}
variable "db_name"          {}
variable "db_user"          {}
variable "db_pass"          {}

provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_service_account" "cron_invoker" {
  account_id   = "mlb-cron-invoker"
  display_name = "MLB Cron Invoker"
}

resource "google_cloud_run_service_iam_member" "invoker_binding" {
  location = var.region
  project  = var.project_id
  service  = var.app_name
  role     = "roles/run.invoker"
  member   = "serviceAccount:${google_service_account.cron_invoker.email}"
}

resource "google_cloud_scheduler_job" "daily_sync" {
  name             = "mlb-daily-sync"
  description      = "Triggers the daily MLB game data ingestion pipeline"
  schedule         = "0 1 * * *"
  time_zone        = "America/New_York"
  attempt_deadline = "320s"

  http_target {
    http_method = "POST"
    uri         = "https://placeholder-url-updated-by-ci-cd.com"

    oidc_token {
      service_account_email = google_service_account.cron_invoker.email
    }
  }
}

resource "google_sql_database_instance" "postgres_instance" {
  name             = var.db_instance_name
  database_version = "POSTGRES_18"
  region           = var.region

  settings {
    edition = "ENTERPRISE"
    tier = "db-f1-micro"
    ip_configuration {
      ipv4_enabled = true
    }
  }
  deletion_protection = false
}

resource "google_sql_database" "mlb_db" {
  name     = var.db_name
  instance = google_sql_database_instance.postgres_instance.name
}

resource "google_sql_user" "db_user" {
  name     = var.db_user
  instance = google_sql_database_instance.postgres_instance.name
  password = var.db_pass
}
