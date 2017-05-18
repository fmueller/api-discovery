FROM registry.opensource.zalan.do/stups/node:7.10-cd50

RUN apt-get update && apt-get install -y apt-transport-https \
    && curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
    && echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update && apt-get install -y yarn \
    && rm -rf /var/lib/apt/lists/*

ADD package.json /opt/api-discovery-ui/
ADD yarn.lock /opt/api-discovery-ui/
ADD dist/client /opt/api-discovery-ui/client
ADD dist/server /opt/api-discovery-ui/server

WORKDIR /opt/api-discovery-ui

RUN yarn install --production

ENV NODE_ENV production

CMD ["node", "server/index.js"]
