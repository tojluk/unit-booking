#!/bin/bash

echo "Stopping database..."
podman compose down

echo "Starting database..."
podman compose up -d
