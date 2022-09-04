package com.example.ddrealtimemanager.shared

import android.content.ContentValues.TAG
import android.util.Log
import com.example.ddrealtimemanager.dm.real_time.DMRealTimeGameActivity
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FireBaseHelper {
     companion object {

         val BASE_DIR = "dnd_realtime"
         val GAMES_DIR = "games"
         val PLAYERS_DIR = "players"
         val FIGHT_DIR = "fight"

         val firebase =
             FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")

         var lastSnapshot: DataSnapshot? = null

         //DM SECTION

         val ref = firebase.getReference(BASE_DIR)
         val gamesRef = ref.child(GAMES_DIR)


         var allListener: ValueEventListener = ref.addValueEventListener(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 lastSnapshot = snapshot
             }

             override fun onCancelled(error: DatabaseError) {
                 //TODO("Not yet implemented")
             }

         })


         fun fbCreateNewGame(game: Game): String {
             val newGameRef = gamesRef.push()
             newGameRef.setValue(game)

             val fbGameId = newGameRef.key

             return fbGameId!!

         }

         fun fbUpdateGame(fbGameId: String, game: Game) {
             val gameRef = gamesRef.child(fbGameId)
             gameRef.child("description").setValue(game.description)
             gameRef.child("image").setValue(game.image)
             gameRef.child("subtitle").setValue(game.subtitle)
         }



         fun fbDeleteGame(fbGameId: String) {
            gamesRef.child(fbGameId).removeValue()
         }


         fun fbCreateNewFight(fightersFBList: ArrayList<String>, fbGameId: String){





             val gameRef = gamesRef.child(fbGameId)
             val fightRef = gameRef.child(FIGHT_DIR)


             fightersFBList.forEach {
                 fightRef.child(it).setValue(false)
             }

         }

         fun fbEndFight(fbGameId: String){
             val gameRef = gamesRef.child(fbGameId)

             val fightRef = gameRef.child(FIGHT_DIR)

             fightRef.removeValue()
         }

         fun fbAddCharactersMidFight(newFighters: ArrayList<String>, fbGameId: String){
             val gameRef = gamesRef.child(fbGameId)
             val fightRef = gameRef.child(FIGHT_DIR)


             newFighters.forEach {
                 fightRef.child(it).setValue(false)
             }
         }

         fun fbFightSetTurn(previousFbCharId: String?, nextFbCharId: String, fbGameId: String){
             val gameRef = gamesRef.child(fbGameId)
             val fightRef = gameRef.child(FIGHT_DIR)

             if(previousFbCharId != null) {
                 fightRef.child(previousFbCharId).setValue(false)
             }
             fightRef.child(nextFbCharId).setValue(true)
         }


         fun fbPushCharacter(character: RT_Character, fbGameId: String): String {

             val currentGameRef = gamesRef.child(fbGameId)
             val playersDirRef = currentGameRef.child(PLAYERS_DIR)

             val characterRef = playersDirRef.push()
             character.firebaseId = characterRef.key
             characterRef.setValue(character)

             val fbCharId = characterRef.key

             return fbCharId!!

         }

         fun fbUpdatecharacter(character: RT_Character, fbGameId: String){
             val currentGameRef = gamesRef.child(fbGameId)
             val playersDirRef = currentGameRef.child(PLAYERS_DIR)

             val thisPlayerRef = playersDirRef.child(character.firebaseId!!)
             thisPlayerRef.setValue(character)
         }

         fun fbUpdateCharacterHealth(newCurrentHP: Int, fbCharId: String, fbGameId: String){
             val currentGameRef = gamesRef.child(fbGameId)
             val playersDirRef = currentGameRef.child(PLAYERS_DIR)

             val thisPlayerRef = playersDirRef.child(fbCharId)
             thisPlayerRef.child("currentHp").setValue(newCurrentHP)
         }

         fun fbRemoveCharacterFromGame(fbCharId: String, fbGameId: String){
             val playerRef = gamesRef.child(fbGameId).child(PLAYERS_DIR).child(fbCharId)
             playerRef.removeValue()
         }


         fun fbCheckGameExists(fbGameId: String): Boolean?{

             var exists: Boolean? = null

             val gameRef = gamesRef.child(fbGameId)

             tick()
             exists = lastSnapshot?.child(GAMES_DIR)?.child(fbGameId)?.exists()





            //while(exists == null){}

             return exists
         }

         fun tick(){
             ref.child("tick").setValue("Tick")
         }


         //TODO Delete this Bad Bad function
         fun fbGetGameInfo(fbGameId: String): Game?{

             var game: Game? = null

             val gameRef = gamesRef.child(fbGameId)

             ref.child("tick").setValue("Tick")




                     val gameName: String = lastSnapshot?.child(GAMES_DIR)?.child(fbGameId)?.child("name")
                         ?.getValue(String::class.java)!!
                     val gameDescr: String = lastSnapshot?.child(GAMES_DIR)?.child(fbGameId)?.child("description")
                         ?.getValue(String::class.java)!!
                     val gameImage: String = lastSnapshot?.child(GAMES_DIR)?.child(fbGameId)?.child("image")
                         ?.getValue(String::class.java)!!
                     val gameSubtitle: String = lastSnapshot?.child(GAMES_DIR)?.child(fbGameId)?.child("subtitle")
                         ?.getValue(String::class.java)!!


                     game = Game(-1, gameName, gameSubtitle, gameDescr, gameImage, fbGameId)





             return game

         }


     }

}