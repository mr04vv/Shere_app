package shareApp.model

import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*


object Task_t : Table("tasks") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 50)
    val group_id = integer("group_id")
    val user_id = integer("user_id")
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
        var user_id: Int = 0,
        var done: Boolean = false,
        var dead: DeadLine = DeadLine()
)

data class Tasks(
        var task: Task? = null
)

data class TaskList(
        var main: MutableList<Tasks>? = null
)

fun getTask(id: Int): Task {

    lateinit var task: Task
    transaction {
        Task_t.select {
            Task_t.id.eq(id)
        }.forEach {
                    task = Task(it[Task_t.id], it[Task_t.title]
                            , it[Task_t.group_id], it[Task_t.user_id], it[Task_t.done],
                            DeadLine(it[Task_t.d_year], it[Task_t.d_month], it[Task_t.d_day]))
                }
    }
    if (task.id == 0) throw halt(404, "is not exist")
    return task
}

fun getTaskListByGroupId(group_id: Int): TaskList {

    lateinit var task: Task
    val list: MutableList<Tasks> = mutableListOf()
    var tasks: TaskList = TaskList()
    lateinit var main: Tasks
    transaction {
        Task_t.select {
            Task_t.group_id.eq(group_id)
        }.forEach {
                    task = Task(it[Task_t.id], it[Task_t.title],
                            it[Task_t.group_id], it[Task_t.user_id], it[Task_t.done],
                            DeadLine(it[Task_t.d_year], it[Task_t.d_month], it[Task_t.d_day]))
                    main = Tasks(task)
                    list += main
                    tasks = TaskList(list)
                }
    }
    return tasks
}

fun getTaskListByUserId(user_id: Int): TaskList {

    lateinit var task: Task
    val list: MutableList<Tasks> = mutableListOf()
    var tasks: TaskList = TaskList()
    lateinit var main: Tasks
    transaction {
        Task_t.select {
            Task_t.user_id.eq(user_id)
        }.forEach {
                    task = Task(it[Task_t.id], it[Task_t.title],
                            it[Task_t.group_id], it[Task_t.user_id], it[Task_t.done],
                            DeadLine(it[Task_t.d_year], it[Task_t.d_month], it[Task_t.d_day]))
                    main = Tasks(task)
                    list += main
                    tasks = TaskList(list)
                }
    }
    return tasks
}

fun addTask(task: Task): Task {

    transaction {
        task.id = Task_t.insert {
            it[title] = task.title
            it[group_id] = task.group_id
            it[user_id] = task.user_id
            it[done] = task.done
            it[d_year] = task.dead.year
            it[d_month] = task.dead.month
            it[d_day] = task.dead.day
        } get Task_t.id
    }
    return task
}
