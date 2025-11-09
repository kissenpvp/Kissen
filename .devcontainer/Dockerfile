# general
FROM debian:trixie
ENV DEBIAN_FRONTEND=noninteractive LANG=C.UTF-8

# download needed things
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        git sudo openjdk-21-jdk && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# user and workspace
RUN useradd -ms /bin/bash kissen && echo "kissen ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

USER kissen
WORKDIR /home/kissen/Kissen