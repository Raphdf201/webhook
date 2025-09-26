package net.raphdf201


val projects = listOf(
    "musicapi",
    "chat",
    "webhook"
)
val statuses = projects.toMutableList().addAll(listOf(
    "ssh"
))
val recentDeploys: MutableList<String> = emptyList<String>().toMutableList()

fun trimList(list: MutableList<String>, maxLength: Int): List<String> {
    while (list.size > maxLength) {
        list.removeFirst()
    }
    return list
}
