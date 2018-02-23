package shareApp.model

import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*


object Task_t : Table("tasks") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 50)
    val group_id = integer("group_id")
    val done = bool("done")
    val d_year = integer("dead_year").nullable()
    val d_month = integer("dead_month").nullable()
    val d_day = integer("dead_day").nullable()
}

data class DeadLine(
        var year: Int? = null,
        var month: Int? = null,
        var day: Int? = null
)

data class Task(
        var id: Int? = 0,
        var title: String = "",
        var group_id: Int = 0,
        var done: Boolean = false,
        var dead: DeadLine = DeadLine()
)

data class Tasks(
        var main: Task? = null
)

fun getTask(id: Int): Task {

    lateinit var task: Task
    transaction {
        Task_t.select {
            Task_t.id.eq(id)
        }.forEach {
                    task = Task(it[Task_t.id], it[Task_t.title]
                            , it[Task_t.group_id], it[Task_t.done],
                            DeadLine(it[Task_t.d_year], it[Task_t.d_month], it[Task_t.d_day]))
                }
    }
    if (task.id == 0) throw halt(404, "is not exist")
    return task
}

fun getTaskListbyId(id: Int): MutableList<Tasks> {

    lateinit var task: Task
    val tasks: MutableList<Tasks> = mutableListOf()
    lateinit var main: Tasks
    transaction {
        Task_t.select {
            Task_t.group_id.eq(id)
        }.forEach {
                    task = Task(it[Task_t.id], it[Task_t.title],
                            it[Task_t.group_id], it[Task_t.done],
                            DeadLine(it[Task_t.d_year], it[Task_t.d_month], it[Task_t.d_day]))
                    main = Tasks(task)
                    tasks += main
                }
    }
    return tasks
}

fun addTask(task: Task): Task {

    transaction {
        task.id = Task_t.insert {
            it[title] = task.title
            it[group_id] = task.group_id
            it[done] = task.done
            it[d_year] = task.dead.year
            it[d_month] = task.dead.month
            it[d_day] = task.dead.day
        } get Task_t.id
    }
    return task
}