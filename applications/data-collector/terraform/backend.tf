terraform {
  backend "gcs" {
    prefix = "terraform/state/data-collector"
  }
}