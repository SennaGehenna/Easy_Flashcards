package io.github.tormundsmember.easyflashcards.ui.base_ui

import androidx.fragment.app.FragmentManager
import com.zhuinden.simplestack.StateChange

class FragmentStateChanger {

  private val fragmentManager: FragmentManager
  private val containerId: Int
  private val forwardAnimation: AnimationSet?
  private val backwardAnimation: AnimationSet?

  constructor(fragmentManager: FragmentManager, containerId: Int, forwardAnimation: AnimationSet, backwardAnimation: AnimationSet) {
    this.fragmentManager = fragmentManager
    this.containerId = containerId
    this.forwardAnimation = forwardAnimation
    this.backwardAnimation = backwardAnimation
  }

  constructor(fragmentManager: FragmentManager, containerId: Int) {
    this.fragmentManager = fragmentManager
    this.containerId = containerId
    this.forwardAnimation = null
    this.backwardAnimation = null
  }

  fun handleStateChange(stateChange: StateChange) {
    val fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack()
    val topNewState = stateChange.topNewState<BaseKey>()

    if (stateChange.direction == StateChange.FORWARD) {
      forwardAnimation?.apply {
        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
      }
    } else if (stateChange.direction == StateChange.BACKWARD) {
      backwardAnimation?.apply {
        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
      }
    }

    for (oldKey in stateChange.getPreviousState<BaseKey>()) {
      val fragment = fragmentManager.findFragmentByTag(oldKey.fragmentTag)
      if (fragment != null) {
        if (!stateChange.getNewState<BaseKey>().contains(oldKey)) {
          fragmentTransaction.remove(fragment)
        } else if (!fragment.isDetached) {
          fragmentTransaction.detach(fragment)
        }
      }
    }
    for (newKey in stateChange.getNewState<BaseKey>()) {
      var fragment = fragmentManager.findFragmentByTag(newKey.fragmentTag)
      if (newKey == topNewState) {
        if (fragment != null) {
          if (fragment.isDetached) {
            fragmentTransaction.attach(fragment)
          }
        } else {
          fragment = newKey.newFragment()
          fragmentTransaction.add(containerId, fragment, newKey.fragmentTag)
        }
      } else {
        if (fragment != null && !fragment.isDetached) {
          fragmentTransaction.detach(fragment)
        }
      }
    }
    fragmentTransaction.commitNow()
  }
}