package top.itfinally.security

import com.google.common.collect.Sets
import com.google.common.eventbus.EventBus

class EventManager {
  companion object {
    private val listenerInstances = Sets.newConcurrentHashSet<Any>()

    fun register(eventBus: EventBus, listener: Any) {
      if (isOnListening(listener)) {
        return
      }

      synchronized(listener) {
        if (isOnListening(listener)) {
          return
        }

        listenerInstances.add(listener)
        eventBus.register(listener)
      }
    }

    private fun isOnListening(listener: Any): Boolean {
      return listenerInstances.contains(listener)
    }
  }
}

data class RolePermissionRefreshEvent(val roleId: String)

data class AccountChangeEvent(val account: String)

data class AccountResetEvent(val account: String)