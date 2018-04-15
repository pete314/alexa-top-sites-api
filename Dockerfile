# Copyright (C) 2018 Peter Nagy (https://peternagy.ie)
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
# =========================================================
# @author     Peter Nagy <pnagy@alison.com>
# @since      03, 2018
# @version    0.1
# @description Dockerfile - a docker image build for the app
# 
FROM openjdk:8-jre-alpine

# Move the application into container
RUN mkdir -p /opt/alexa-top-api/lib && \
    mkdir -p /opt/alexa-top-api/config && \
    apk add java-cacerts

ADD target/lib /opt/alexa-top-api/lib
ADD ./config /opt/alexa-top-api/config

COPY target/alexa-top-api-1.0.jar /opt/alexa-top-api
ENV KEY_STORE_PATH=/opt/alexa-top-api/config/selfsigned.jks
ENV THRUST_STORE_PATH=/etc/ssl/certs/java/cacerts
ENV SERVER_HOST=0.0.0.0

EXPOSE 8888 4443
CMD java -jar -server /opt/alexa-top-api/alexa-top-api-1.0.jar
