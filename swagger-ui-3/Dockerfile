FROM registry.opensource.zalan.do/stups/node:7.10.0-23

MAINTAINER Zalando SE

RUN apt-get update && apt-get install --no-install-recommends -y \
        apt-transport-https=1.2.20 \
    && curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
    && echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update && apt-get install --no-install-recommends -y \
        yarn=0.24.6-1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/api-portal
COPY package.json yarn.lock conf/production.yaml dist ./
RUN yarn install --production && yarn cache clean

ENV API_PORTAL_CONF /opt/api-portal/production.yaml
ENV NODE_ENV production

CMD ["node", "server/index.js"]
