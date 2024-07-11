# Use the official Ubuntu base image
FROM ubuntu:20.04

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Update and install necessary packages
RUN apt-get update && apt-get install -y \
    mysql-server \
    postgresql \
    postgresql-contrib \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set up MySQL
RUN service mysql start && \
    mysqld --skip-grant-tables & \
    sleep 5 && \
    mysql -e "UPDATE mysql.user SET authentication_string='' WHERE user='root';" && \
    mysql -e "FLUSH PRIVILEGES;" && \
    mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';" && \
    mysqladmin -u root -proot shutdown

# Set up PostgreSQL
USER postgres
RUN /etc/init.d/postgresql start && \
    psql --command "ALTER USER postgres WITH PASSWORD 'root';"

# Switch back to root user
USER root

# Create start script
RUN echo '#!/bin/bash\nservice mysql start\nservice postgresql start\ntail -f /dev/null' > /start.sh && \
    chmod +x /start.sh

# Expose ports
EXPOSE 3306 5432

# Set the entry point to the start script
CMD ["/start.sh"]
