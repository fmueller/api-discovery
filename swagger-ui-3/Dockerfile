FROM registry.opensource.zalan.do/stups/node:7.10-cd52

MAINTAINER Zalando SE

RUN apt-get update && apt-get install --no-install-recommends -y \
        apt-transport-https=1.2.20 \
    && curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
    && echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update && apt-get install --no-install-recommends -y \
        yarn=0.24.6-1 \
    && rm -rf /var/lib/apt/lists/*

COPY package.json /opt/api-discovery-ui/
COPY yarn.lock /opt/api-discovery-ui/

WORKDIR /opt/api-discovery-ui
RUN yarn install --production && yarn cache clean

COPY dist /opt/api-discovery-ui/
RUN mv /opt/api-discovery-ui/scm-source.json /

ARG conf=conf/production.yaml
ENV API_DISCOVERY_CONF /opt/api-discovery-ui/conf.yaml
COPY $conf $API_DISCOVERY_CONF

ENV NODE_ENV production

CMD ["node", "server/index.js"]
