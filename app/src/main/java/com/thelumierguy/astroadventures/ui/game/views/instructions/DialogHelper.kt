package com.thelumierguy.astroadventures.ui.game.views.instructions

class DialogHelper {

    var counter = 0

    var isOpen = true

    fun getItemSize() = dialogList.size

    private val dialogList by lazy {
        mutableListOf<Dialog>().apply {
            add(Dialog("Greetings Ranger!. It's good to have you back after so long. As you know, our planet's resources are all but depleted."))
            add(Dialog("It is our job to go on this mission to help our world. I assume you remember the workings around here. But let's go over it again, shall we?"))
            add(Dialog("Please tilt your phone to control your ship.", InstructionType.TILT))
            add(Dialog("Good Job. Try conserving your resources as they are hard to come by these days."))
            add(Dialog("At the top-left, you'll find information regarding your Ammunition reserves.",
                InstructionType.AMMO))
            add(Dialog("Try conserving your ammo as much as possible. Once it's empty, you cannot refill it and you'll be a sitting duck!",
                InstructionType.AmmoWarning, duration = 5000L))
            add(Dialog("And at the bottom, you'll find information regarding your Spaceship's health.",
                InstructionType.HEALTH))
            add(Dialog("Now tap on your screen to fire a missile.", InstructionType.FIRE))
            add(Dialog("Good Job!", duration = 4000L))
            add(Dialog("Hold on! There's an incoming transmission."))
            add(Dialog("Mayday! Mayday! Someone just fired a missile and our location has been compromised. \nNow, all of China knows we're here.",
                duration = 12000L))
            add(Dialog("There's a fleet of Space Pirates heading your way. Finish them off as soon as possible."))
            add(Dialog("You'll occasionally see an enemy drop a Blue Capsule. Be sure to collect it to gather more ammunition.",
                InstructionType.EnemySpotted))
            add(Dialog("Good Luck!", InstructionType.EnemyTranslated, duration = 3000L))
        }
    }

    fun getNextDialog(): Dialog? {
        return if (counter < dialogList.size && isOpen) {
            val dialog = dialogList[counter]
            counter++
            dialog
        } else {
            null
        }
    }

    fun setLock() {
        isOpen = false
    }

    fun removeLock() {
        isOpen = true
    }

    data class Dialog(
        val text: String,
        val type: InstructionType = InstructionType.TEXT,
        val duration: Long = 8000L,
    )

    enum class InstructionType {
        TEXT,
        AMMO,
        HEALTH,
        TILT,
        FIRE,
        EnemySpotted,
        EnemyTranslated,
        AmmoWarning
    }
}
