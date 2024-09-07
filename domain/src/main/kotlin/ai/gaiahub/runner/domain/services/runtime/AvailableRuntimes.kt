package ai.gaiahub.runner.domain.services.runtime

import ai.gaiahub.runner.domain.models.GaiaRuntime

val NODE_JS = GaiaRuntime(
    name = "sandbox-node",
    version = "0.0.1",
    description = "NodeJS runtime for running sandboxed code",
    imageName = "sandbox-node",
    baseImage = "node:18-slim",
    defaultDeployCommand = "node index.js"
)

val LINUX = GaiaRuntime(
    name = "sandbox-alpine",
    version = "0.0.1",
    description = "Linux alpine runtime for running sandboxed code",
    imageName = "sandbox-alpine",
    baseImage = "alpine:3.20.2",
    defaultDeployCommand = "/bin/sh run.sh"
)