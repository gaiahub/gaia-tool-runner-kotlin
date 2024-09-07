package ai.gaiahub.runner


fun Array<String>.asMap(): Map<String, String> {
    return this.toList().chunked(2).associate { it[0] to it[1] }
}