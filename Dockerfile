# Base image
FROM oraclelinux:9

# Set working directory
WORKDIR /app

# Install required tools
RUN microdnf install -y \
    tar \
    gzip \
    curl \
    && microdnf clean all

# Download Oracle JDK 23 (requires Oracle license acceptance)
RUN curl -L -o jdk.tar.gz \
    https://download.oracle.com/java/23/latest/jdk-23_linux-x64_bin.tar.gz \
    && tar -xzf jdk.tar.gz \
    && rm jdk.tar.gz

# Set JAVA_HOME and PATH
ENV JAVA_HOME=/app/jdk-23
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy application JAR
COPY target/newsApi-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
