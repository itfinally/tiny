package top.itfinally.core;

import java.util.Objects;
import java.util.function.Supplier;

// 按 kotlin 的使用方式封装空安全工具
public class NullSafety {
  private NullSafety() {
  }

  // callback?.call()
  public static <T> T call( Supplier<T> callback ) {
    Objects.requireNonNull( callback, "Callback require not null." );

    try {
      return callback.get();

    } catch ( NullPointerException exp ) {
      return null;
    }
  }

  // preAction.a() ?: reserve[0].b() ?: reserve[1].c() ?: null
  @SafeVarargs
  public static <T> T conditional( Supplier<T> preAction, Supplier<T>... reserve ) {
    Objects.requireNonNull( preAction, "PreAction require not null." );
    Objects.requireNonNull( reserve, "Reserve require not null." );

    T result = call( preAction );
    if ( result != null ) {
      return result;
    }

    for ( Supplier<T> it : reserve ) {
      if ( null == it ) {
        continue;
      }

      try {
        result = it.get();

      } catch ( NullPointerException exp ) {
        result = null;
      }

      if ( result != null ) {
        return result;
      }
    }

    return null;
  }

  // target as? type
  @SuppressWarnings( "unchecked" )
  public static <T> T as( Class<T> type, Object target ) {
    Objects.requireNonNull( type, "Type require not null." );

    return null == target ? null : target.getClass().isAssignableFrom( type ) ? ( T ) target : null;
  }
}