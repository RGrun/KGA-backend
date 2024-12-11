package guru.furu.kgaBackend.adapter.model

enum class RelationshipType(val serValue: String) {
    HAS_AUTH(":HAS_AUTH"),
    IS_ADMIN(":IS_ADMIN"),
    UPLOADED(":UPLOADED"),
    COMMENTED(":COMMENTED"),
    HAS_TAG(":HAS_TAG"),
    HAS_LIST(":HAS_LIST"),
    IN_LIST(":IN_LIST"),
    HAS_COMMENT(":HAS_COMMENT"),
    FOLLOWS(":FOLLOWS"),
    SESSION(":SESSION"),
}
