package tutorial.webapp.binding

import com.thoughtworks.binding.{Binding, Route, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import org.scalajs.dom.ext.{KeyCode, LocalStorage}
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, KeyboardEvent, Node, window}
import upickle.default.{read, write}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("BindingTodo")
object Todo {

  final class Todo(val title    : String,
                   val completed: Boolean)

  object Todo {
    def apply(title: String, completed: Boolean) = new Todo(title, completed)

    def unapply(todo: Todo): Option[(String, Boolean)] = Option((todo.title,todo.completed))
  }

  final case class TodoList(text:String,hash:String,
                            items:BindingSeq[Todo])

  object Models {
    val LocalStorageName = "todos-binding.scala"

    def load() = LocalStorage(LocalStorageName).toSeq.flatMap(read[Seq[Todo]])
    def save(todos:Seq[Todo]) = LocalStorage(LocalStorageName) = write(todos)

    val allTodos = Vars[Todo](load(): _*)
    val autoSave: Binding[Unit] = Binding{save(allTodos.all.bind)}
    autoSave.watch()

    val editingTodo = Var[Option[Todo]](None)

    val all = TodoList("All","#/",allTodos)
    val active = TodoList("Active","#/active",for (todo ← allTodos if !todo.completed) yield todo)
    val completed = TodoList("Completed","#/completed",for (todo ← allTodos if todo.completed) yield todo)
    val todoLists = Vector(all,active,completed)
    val route = Route.Hash(all)(new Route.Format[TodoList]{
      override def unapply(hashText:String) = todoLists.find(_.hash == window.location.hash)

      override def apply(state: TodoList): String = state.hash
    })

    route.watch()
  }

  import Models._

  @dom
  def header:Binding[Node] = {
    val keyDownHandler = {event:KeyboardEvent ⇒
      (event.currentTarget,event.keyCode) match {
        case (input:HTMLInputElement,KeyCode.Enter) ⇒
          input.value.trim match {
            case "" ⇒
            case title ⇒
              allTodos.value += Todo(title,completed=false)
              input.value = ""
          }
        case _⇒
      }
    }

    <header class="header">
      <h1>todos</h1>
      <input class="new-todo" autofocus={true} placeholder="What needs to be done?" onkeydown={keyDownHandler}/>
    </header>
  }

  @dom
  def todoListItem(todo:Todo):Binding[Node] = {
    val suppressOnBlur = Var(false)

    def submit = {event:Event ⇒
      suppressOnBlur.value = true
      editingTodo.value = None
      event.currentTarget.asInstanceOf[HTMLInputElement].value.trim match {
        case "" ⇒
          allTodos.value.remove(allTodos.value.indexOf(todo))
        case trimmedTitle ⇒
          allTodos.value(allTodos.value.indexOf(todo)) = Todo(trimmedTitle,todo.completed)
      }
    }

    def keyDownHandler = {event:KeyboardEvent ⇒
      event.keyCode match {
        case KeyCode.Escape ⇒
          suppressOnBlur.value = true
          editingTodo.value = None
        case KeyCode.Enter ⇒
          submit(event)
        case _⇒
      }
    }
    def blurHandler = Binding[Event ⇒ Any]{
      if (suppressOnBlur.bind) Function.const(()) else submit
    }

    def toggleHandler = {event:Event ⇒
      allTodos.value(allTodos.value.indexOf(todo)) = Todo(todo.title,event.currentTarget.asInstanceOf[HTMLInputElement].checked)
    }

    <li class={s"${if (todo.completed) "completed" else ""} ${if (editingTodo.bind.contains(todo)) "editing" else ""}"}>
      <div class="view">
        <input class="toggle" type="checkbox" checked={todo.completed} onclick={toggleHandler}/>
        <label ondblclick={_:Event ⇒ editingTodo.value = Some(todo);editInput.focus()}>{todo.title}</label>
      </div>
      <input id="editInput" class="edit" value={todo.title} onblur={blurHandler.bind} onkeydown={keyDownHandler}/>
    </li>
  }

  @dom
  def mainSection:Binding[Node] = {
    def toggleAllClickHandler = {event:Event ⇒
      for ((todo,i) ← allTodos.value.zipWithIndex) {
        if (todo.completed != event.currentTarget.asInstanceOf[HTMLInputElement].checked) {
          allTodos.value(i) = Todo(todo.title,event.currentTarget.asInstanceOf[HTMLInputElement].checked)
        }
      }
    }

    <section class="main" style:display={if (allTodos.length.bind == 0) "none" else ""}>
      <input type="checkbox" class="toggle-all" checked={active.items.length.bind ==0} onclick={toggleAllClickHandler}/>
      <label for="toggle-all">Mark all as complete</label>
      <ul class="todo-list">{for (todo <- route.state.bind.items) yield todoListItem(todo).bind }</ul>
    </section>
  }

  @dom
  def footer:Binding[Node] = {
    def clearCompletedClickHandler = {_:Event ⇒
      allTodos.value --= (for (todo ← allTodos.value if todo.completed) yield todo)
    }
    <footer class="footer" style:display = { if (allTodos.length.bind == 0) "none" else ""}>
      <span class="todo-count">
    <strong>{active.items.length.bind.toString}</strong>
    {if (active.items.length.bind == 1) "item" else "items"} left
      </span>
    <ul class="filters">
      {
      import scalaz.std.vector.vectorInstance
        for (todoList <- todoLists) yield {
          <li>
            <a href={todoList.hash} class ={if (todoList == route.state.bind) "selected" else ""}>
              {todoList.text}
            </a>
          </li>
        }
      }
    </ul>
    <button class="clear-completed" onclick={clearCompletedClickHandler}
    style:visibility={if (completed.items.length.bind == 0) "hidden" else "visible"}>
      Clear completed
    </button>
    </footer>
  }

  @dom
  def todoapp:Binding[BindingSeq[Node]] = {
    <section class="todoapp">{header.bind}{mainSection.bind}{footer.bind}</section>
    <footer class="info">
      <p>Double-click to edit a todo</p>
      <p>Written by </p>
    </footer>
  }

  @JSExport
  def main(container:Node) = dom.render(container,todoapp)
}
