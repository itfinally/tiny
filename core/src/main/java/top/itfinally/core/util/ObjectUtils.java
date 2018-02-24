package top.itfinally.core.util;

import java.util.function.Supplier;

public final class ObjectUtils {
  private ObjectUtils() {
  }

  public static <T> T getOrDefault( T result, T defaultVal ) {
    return null == result ? defaultVal : result;
  }

  public static <T> T getOrDefault( Supplier<T> action, T defaultVal ) {
    try {
      return action.get();

    } catch ( NullPointerException e ) {
      return defaultVal;
    }
  }
}
