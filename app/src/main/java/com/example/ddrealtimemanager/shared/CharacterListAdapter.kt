import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Character
import com.example.ddrealtimemanager.shared.Game
import kotlinx.android.synthetic.main.layout_character_item.view.*
import kotlinx.android.synthetic.main.layout_game_card_item.view.*

class CharacterListAdapter(private val context: Context, private var charactersList: List<Character>): BaseAdapter() {
    override fun getCount(): Int {
        return charactersList.count()
    }

    override fun getItem(position: Int): Character {
        return charactersList[position]
    }

    override fun getItemId(position: Int): Long {
        val character: Character = charactersList[position]
        return character.id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var newView = convertView

        if(newView == null)
            newView = LayoutInflater.from(context).inflate(
                R.layout.layout_character_item, parent, false
            )

        val character = charactersList[position]
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.propic_standard)
            .error(R.drawable.propic_standard)

        Glide.with(context)
            .applyDefaultRequestOptions(requestOptions)
            .load(character.image)
            .into(newView!!.card_iv_player_image)

        newView.card_tv_player_name?.text = character.name
        newView.card_tv_player_description?.text = character.clas + " " + character.race

        return newView
    }

}