package io.martins.valhalla.command.nested;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ErrorHandler {

  private final Map<Class<? extends Throwable>, Command> map = new HashMap<>();

  private final List<Class<? extends Throwable>> classExceptions = new ArrayList<>();

  private Command commandDefault = null;

  public abstract void handleError(final Context context, final Throwable e);

  public <T extends Throwable> ErrorHandler whenException(final Class<T> clazzException) {
    classExceptions.add(clazzException);

    return this;
  }

  public ErrorHandler doProcess(final Command command) {
    classExceptions.forEach(ce -> map.put(ce, command));
    classExceptions.clear();

    return this;
  }

  public ErrorHandler doNothing() {
    classExceptions.forEach(ce -> map.put(ce, ctx -> { /* */ }));
    classExceptions.clear();

    return this;
  }

  public void withDefault(final Command command) {
    this.commandDefault = command;
  }

  public void execute(final Context context, final Throwable exception) {
    try {
      final Command command = map.getOrDefault(exception.getClass(), this.commandDefault);

      if (command != null) {
        command.execute(context);
      }

    } finally {
      map.clear();
      classExceptions.clear();
      this.commandDefault = null;
    }
  }

}
