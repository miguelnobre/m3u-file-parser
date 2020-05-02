#!/bin/sh

name=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="artifactId"]/text()' pom.xml)
version=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml)
commit_hash=$(git log -1 '--pretty=format:%H' | head -c 7)

docker build \
--build-arg NAME=${name} \
--build-arg VERSION=${version} \
--tag ${name}:latest \
--tag ${name}:${version} \
--tag ${name}:${commit_hash} \
--label ${name}=${version} .
