package net.raphdf201

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import kotlinx.css.CssBuilder
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.ul
import java.lang.Exception

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title("Dashboard")
                    link("https://assets.raphdf201.net/raphdf201.css", "stylesheet")
                }
                body {
                    h1("Hello")
                    ul {
                        li { a("/status") { +"Services" } }
                        li { a("/deploy") { +"Deployments" } }
                    }
                    script(src = "https://assets.raphdf201.net/dark.js") {}
                }
            }
        }

        get("/status") {
            call.respondHtml {
                head {
                    title("Status")
                    link("https://assets.raphdf201.net/raphdf201.css", "stylesheet")
                }
                body {
                    h1 { +"Status" }
                    ul {
                        statuses.forEach {
                            h2 { +it }
                            p { +getStatus(it) }
                        }
                    }
                    script(src = "https://assets.raphdf201.net/dark.js") {}
                }
            }
        }

        get("/deploy") {
            val amnt: Int = try {
                call.queryParameters["amount"].toString().toInt()
            } catch (e: Exception) {
                10
            }
            call.respondHtml {
                body {
                    ul {
                        trimList(recentDeploys, amnt).forEach {
                            li {
                                +it
                            }
                        }
                    }
                }
            }
        }

        get("/deploy/{project}") {
            val proj = call.parameters["project"]
            if (proj == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            recentDeploys.addLast(proj)
            Runtime.getRuntime().exec(arrayOf("systemctl", "start", "deploy@$proj"))
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun getStatus(service: String): String {
    val pb = ProcessBuilder("systemctl", "status", service).redirectErrorStream(true)
    return pb.start().inputStream.bufferedReader().readText()
}
