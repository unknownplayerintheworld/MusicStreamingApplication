package com.hung.musicstreamingapplication.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.MusicApplication
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Comment
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.data.repository.AuthorRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.CommentRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.PlaylistRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.SongRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.YoutubeRepositoryImpl
import com.hung.musicstreamingapplication.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class MusicViewModel @Inject constructor(
    application: Application,
    private val songRepository: SongRepositoryImpl,
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl,
    private val playlistRepositoryImpl: PlaylistRepositoryImpl,
    private val authorRepository: AuthorRepositoryImpl,
    private val commentRepositoryImpl: CommentRepositoryImpl
) :AndroidViewModel(application = application){

    private val _currentSong = MutableStateFlow(Song())
    val currentSong = _currentSong.asStateFlow()

    private val _isFavPlaylist = MutableStateFlow(false)
    val isFavPlaylist = _isFavPlaylist.asStateFlow()

    private val _currentListSong = MutableStateFlow<MutableList<Song>>(mutableListOf())
    private val _isSave = MutableStateFlow(false)
    val isSave = _isSave.asStateFlow()
    private val _isUpsertSuccessful = MutableStateFlow(false)
    private val _isSuccessfulComment = MutableStateFlow(-1)
    val isSuccessfulComment = _isSuccessfulComment.asStateFlow()
    private val _isReplying = MutableStateFlow(Comment())
    val isReplying = _isReplying.asStateFlow()

    private val _searchSong = MutableStateFlow<List<Song>>(emptyList())
    val searchSong = _searchSong.asStateFlow()
    private val _searchPlaylist = MutableStateFlow<List<Playlist>>(emptyList())
    val searchPlaylist = _searchPlaylist.asStateFlow()
    private val _searchAuthor = MutableStateFlow<List<Author>>(emptyList())
    val searchAuthor = _searchAuthor.asStateFlow()
    private val _searchAlbum = MutableStateFlow<List<Album>>(emptyList())
    val searchAlbum = _searchAlbum.asStateFlow()

    // song from album
    private val _songFromPlaylist = MutableStateFlow<List<Song>>(emptyList())
    val songFromPlaylist = _songFromPlaylist.asStateFlow()
    private val _playlistClicked = MutableStateFlow(Playlist())
    val playlistClicked = _playlistClicked.asStateFlow()
    private val _albumClicked = MutableStateFlow(Album())
    val albumClicked = _albumClicked.asStateFlow()
    private val _isClickedAlbumOrPlaylist = MutableStateFlow(0)
    val isClickedAlbumOrPlaylist = _isClickedAlbumOrPlaylist.asStateFlow()
    private val _isUserCreatedPlaylist = MutableStateFlow(false)
    val isUserCreatedPlaylist = _isUserCreatedPlaylist.asStateFlow()
    private val _createdPlaylistStatus = MutableStateFlow(false)
    val createdPlaylistStatus = _createdPlaylistStatus.asStateFlow()
    private val _searchOnlineSong = MutableStateFlow<List<Song>?>(emptyList())
    private val _searchRecentlySong = MutableStateFlow<List<Song>?>(emptyList())
    val searchOnlineSong = _searchOnlineSong.asStateFlow()
    val searchRecentlySong = _searchRecentlySong.asStateFlow()
    private val _isAddingSuccessful = MutableStateFlow(-1)
    val isAddingSuccessful = _isAddingSuccessful.asStateFlow()
    private val _isRemoveSongFromPlaylist = MutableStateFlow(-1)
    val isRemoveSongFromPlaylist = _isRemoveSongFromPlaylist.asStateFlow()



    //library
    private val _albumRecentlyList = MutableStateFlow<List<Album>?>(emptyList())
    private val _playlistRecentlyList = MutableStateFlow<List<Playlist>?>(emptyList())
    private val _songRecentlyList = MutableStateFlow<List<Song>?>(emptyList())
    private val _authorRecentList = MutableStateFlow<List<Author>?>(emptyList())
    val albumRecentltList = _albumRecentlyList.asStateFlow()
    val playlistRecentlyList = _playlistRecentlyList.asStateFlow()
    val songlistRecently = _songRecentlyList.asStateFlow()
    val authorRecently = _authorRecentList.asStateFlow()
    private val _favAlbums = MutableStateFlow<List<Album>?>(emptyList())
    private val _favPlaylists = MutableStateFlow<List<Playlist>?>(emptyList())
    val favAlbum = _favAlbums.asStateFlow()
    val favPlaylist = _favPlaylists.asStateFlow()

    // song from author
    private val _hotAuthorSong = MutableStateFlow<List<Song>>(emptyList())
    val hotAuthorSong = _hotAuthorSong.asStateFlow()
    private val _author = MutableStateFlow(Author())
    val author = _author.asStateFlow()
    private val _albumAuthor = MutableStateFlow<List<Album>>(emptyList())
    val albumAuthor = _albumAuthor.asStateFlow()
    private val _playlistAuthor = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistAuthor = _playlistAuthor.asStateFlow()
    private val _relatedAuthor = MutableStateFlow<List<Author>>(emptyList())
    val relatedAuthor = _relatedAuthor.asStateFlow()

    private val playbackStateFlow: StateFlow<String> by lazy {
        (application as MusicApplication).playbackStateFlow
    }

    val playbackState: StateFlow<String> = playbackStateFlow


    private val _currentPos = MutableStateFlow(0f)
    val currentPos = _currentPos.asStateFlow()

    private val _currentState = MutableStateFlow(0)
    val currentState = _currentState.asStateFlow()

    private val _keyword = MutableStateFlow<MutableList<String>>(mutableListOf())
    val keyword = _keyword.asStateFlow()


    private val _isNotificationPermissionGranted = MutableStateFlow(false)
    val isNotificationPermissionGranted: StateFlow<Boolean> = _isNotificationPermissionGranted.asStateFlow()

    // Trạng thái yêu cầu quyền
    private val _requestPermissionEvent = MutableStateFlow(false)
    val requestPermissionEvent: StateFlow<Boolean> = _requestPermissionEvent

    private val currentPosition: StateFlow<Long> by lazy {
        (application as MusicApplication).currentPos
    }

    //comment
    private val _comments = MutableStateFlow<List<Comment>?>(emptyList())
    val comment = _comments.asStateFlow()

    //library
    fun getRecentlyList(userID: String,count: Int){
        viewModelScope.launch {
            songRepository.getAlbumRecently(userID,count).let{album->
                _albumRecentlyList.update {
                    album
                }
            }
            songRepository.getPlaylistRecently(userID,count).let{
                playlists ->
                _playlistRecentlyList.update {
                    playlists
                }
            }
            songRepository.getSongRecently(userID,count).let{
                song ->
                _songRecentlyList.update {
                    song
                }
            }
            songRepository.getAuthorRecently(userID,count).let{
                author ->
                _authorRecentList.update {
                    author
                }
            }
        }
    }
    fun getFavListsInLibrary(userID: String){
        viewModelScope.launch {
            playlistRepositoryImpl.getFavouritePlaylists(userID).let { playlist ->
                _favPlaylists.update {
                    playlist
                }
            }
            playlistRepositoryImpl.getFavouriteAlbums(userID).let{albums ->
                _favAlbums.update {
                    albums
                }
            }
        }
    }


    //author screen
    fun getPlaylistsAuthor(author: Author){
        viewModelScope.launch {
            authorRepository.getPlaylistsAuthor(author.id)?.let { list ->
                _playlistAuthor.update {
                    list
                }
            }
        }
    }
    fun getRelatedAuthor(author: Author){
        viewModelScope.launch {
            authorRepository.getTopRelatedAuthors(author.id)?.let {
                list ->
                _relatedAuthor.update {
                    list
                }
            }
        }
    }
    fun getAlbumAuthor(author: Author){
        viewModelScope.launch {
            authorRepository.getAlbumsAuthor(author.id)?.let {
                list ->
                _albumAuthor.update {
                    list
                }
            }
        }
    }
    fun getAuthorHotSong(authorID: String){
        viewModelScope.launch {
            songRepository.getHotAuthorSongs(authorID)?.let{list ->
                _hotAuthorSong.update {
                    list
                }
            }
        }
    }
    fun setAuthor(author : Author){
        _author.update {
            author
        }
    }
    fun playlistClicked(playlist: Playlist){
        _playlistClicked.update {
            playlist
        }
        _isClickedAlbumOrPlaylist.update {
            1
        }
    }
    //playlist
    fun removeSongFromPlaylist(playlist: Playlist,song: Song){
        viewModelScope.launch {
            songRepository.removeSongFromPlaylist(playlist,song).let{
                int ->
                _isRemoveSongFromPlaylist.update {
                    int
                }
            }
        }
    }
    fun addSongToPlaylist(playlistID: String,songID: String){
        viewModelScope.launch {
            songRepository.addSongToPlaylist(playlistID, songID).let {
                status ->
                _isAddingSuccessful.update {
                    status
                }
            }
        }
    }
    fun getOnlineSong(){
        viewModelScope.launch {
            songRepository.getOnlineSong().let {
                song ->
                _searchOnlineSong.update {
                    song
                }
            }
        }
    }
    fun getOnlineSongByKW(kw: String){
        viewModelScope.launch {
            songRepository.getSongRecordFromSearching(kw).let{
                list ->
                _searchOnlineSong.update {
                    list
                }
            }
        }
    }
    fun getRecentSong(userID: String,count: Int){
        viewModelScope.launch {
            songRepository.getSongRecently(userID,count).let{
                song ->
                _searchRecentlySong.update {
                    song
                }
            }
        }
    }
    fun getRecentlySongByKW(userID: String,count: Int,kw: String){
        viewModelScope.launch {
            songRepository.getRecentlySongByKw(kw,count,userID).let{
                song ->
                _searchRecentlySong.update {
                    song
                }
            }
        }
    }

    fun albumClicked(album : Album){
        _albumClicked.update {
            album
        }
        _isClickedAlbumOrPlaylist.update {
            2
        }
    }
    fun checkUserCreatedPlaylist(userID: String,playlist: Playlist){
        viewModelScope.launch {
            playlistRepositoryImpl.getUserCreatedPlaylist(playlist.id,userID).let {
                isUser -> _isUserCreatedPlaylist.update {
                    isUser
            }
            }
        }
    }
    fun createNewPlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistRepositoryImpl.addNewPlaylist(playlist = playlist).let{
                bool ->
                _createdPlaylistStatus.update {
                    bool
                }
            }
        }
    }
    fun setCreatedPlaylistStatus(boolean: Boolean){
        _createdPlaylistStatus.update {
            boolean
        }
    }
    fun setFavouriteStatus(status: Boolean,userID: String,playlistID: String){
        viewModelScope.launch {
            if(status){
                playlistRepositoryImpl.addPlaylistToFavourite(userID,playlistID).apply {
                    _isFavPlaylist.update {
                        this
                    }
                }
            }
            else{
                playlistRepositoryImpl.delPlaylistFromFavourite(userID,playlistID).apply {
                    _isFavPlaylist.update {
                        !this
                    }
                }
            }
        }
    }
    fun getFavouriteStatus(userID:String,playlistID: String){
        viewModelScope.launch{
            playlistRepositoryImpl.getFavouriteStatus(userID,playlistID).apply {
                _isFavPlaylist.update {
                    this
                }
            }
        }
    }
    fun setFavouriteStatusAlbum(status: Boolean,userID: String,albumID: String){
        viewModelScope.launch {
            if(status){
                playlistRepositoryImpl.addAlbumToFavourite(userID,albumID).apply {
                    _isFavPlaylist.update {
                        this
                    }
                }
            }
            else{
                playlistRepositoryImpl.delAlbumFromFavourite(userID,albumID).apply {
                    _isFavPlaylist.update {
                        !this
                    }
                }
            }
        }
    }
    fun getFavouriteAlbumStatus(userID:String,albumID: String){
        viewModelScope.launch{
            playlistRepositoryImpl.getFavouriteStatusAlbum(userID,albumID).apply {
                _isFavPlaylist.update {
                    this
                }
            }
        }
    }
    private fun startAutoUpdatePosition(newDuration: Long){
        _currentPos.value = newDuration.toFloat()
    }
    fun updatePermission(isGranted: Boolean){
        _isNotificationPermissionGranted.value = isGranted
    }

    fun requestPermission() {
        _requestPermissionEvent.value = true
    }
    fun setCurrentSong(song: Song){
        _currentSong.value = song
    }


    init{
        viewModelScope.launch {
            playbackStateFlow.collect {
                if (it.contains("Start")) {
                    _currentState.value = 0
                    Log.d("PLAYBACK_STATE","START")
                } else if (it.contains("Play")) {
                    _currentState.value = 1
                    Log.d("PLAYBACK_STATE","PLAY")
                } else if (it.contains("Pause")) {
                    _currentState.value = 2
                    Log.d("PLAYBACK_STATE","PAUSE")
                } else if(it.contains("Comp")){
                    _currentState.value = 3
                    Log.d("PLAYBACK_STATE","COMP")
                } else {
                    _currentState.value = -1
                }
            }
        }
        viewModelScope.launch {
            currentPosition.collect{
                startAutoUpdatePosition(it)
                Log.d("CurrentPos:",it.toString())
            }
        }
    }
    fun getSongFromPlaylist(playlistID: String){
        viewModelScope.launch {
            songRepository.getSongFromPlaylist(playlistID = playlistID)?.let { songs ->
                _songFromPlaylist.update {
                    songs
                }
            }
        }
    }
    fun getSongFromAlbum(albumID: String){
        viewModelScope.launch {
            songRepository.getSongFromAlbum(albumID = albumID)?.let { songs ->
                _songFromPlaylist.update {
                    songs
                }
            }
        }
    }
    fun setCurrentSongList(list: List<Song>){
        _currentListSong.update {
            list.toMutableList()
        }
    }
    // Hàm để bắt đầu dịch vụ phát nhạc
    fun startMusicService(song: Song,list: List<Song> = emptyList(),index: Int = 0) {
        viewModelScope.launch {
            if(list.isEmpty()) {
                // Lấy danh sách bài hát liên quan
                val relatedSongs = songRepository.getRelatedSong(songID = song.id).toMutableList()

                // Thêm bài hát hiện tại vào đỉnh của danh sách
                relatedSongs.add(0, song)  // Thêm bài hát hiện tại vào đầu danh sách

                // Cập nhật _currentListSong với danh sách mới
                _currentListSong.value = relatedSongs
            }else{
                _currentListSong.update {
                    list.toMutableList()
                }
            }

            // Gửi danh sách sang MusicService
            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, MusicService::class.java).apply {
                action = "ACTION_PLAY"
                putExtra("EXTRA_MUSIC_URI", song)
                // Bạn có thể truyền danh sách bài hát nếu cần
                putParcelableArrayListExtra("EXTRA_SONG_LIST", ArrayList(_currentListSong.value))
                putExtra("INDEX_IN_LIST",index)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
    fun getSongSearchResults(key: String){
        viewModelScope.launch {
            songRepository.getSongRecordFromSearching(key)?.let { songs ->
                _searchSong.update {
                     songs
                }
            }
        }
    }
    fun getAlbumSearchResults(key: String){
        viewModelScope.launch {
            songRepository.getAlbumRecordFromSearching(key)?.let { albums ->
                _searchAlbum.update {
                    albums
                }
            }
        }
    }
    fun getPlaylistSearchResults(key: String){
        viewModelScope.launch {
            songRepository.getPlaylistRecordFromSearching(key)?.let { playlists ->
                _searchPlaylist.update {
                    playlists
                }
            }
        }
    }
    fun getAuthorSearchResults(key: String){
        viewModelScope.launch {
            songRepository.getAuthorRecordFromSearching(key)?.let { authors ->
                _searchAuthor.update {
                    authors
                }
            }
        }
    }
    fun pauseMusic(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_PAUSE"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun resumeMusic(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_RESUME"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun seekTo(position: Long){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_SEEK"
            putExtra("EXTRA_POSITION",position)
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun nextSong(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_NEXT"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun prevSong(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_PREV"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun shuffledSong(isShuffled : Boolean){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_SHUFFLED"
            putExtra("IS_SHUFFLED",isShuffled)
        }
        ContextCompat.startForegroundService(context, intent)
    }
    fun repeatSong(repeatMode: Int){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_REPEATED"
            putExtra("REPEAT_MODE",repeatMode)
        }
        ContextCompat.startForegroundService(context, intent)
    }
    fun getMostRecentlySong(userID: String){
        viewModelScope.launch {
            songRepository.getMostRecentlySong(userID)?.let { mostRecentSong ->
                _currentSong.update { mostRecentSong }
            }
        }
    }
    fun getKeyword(){
        viewModelScope.launch {
                youtubeRepositoryImpl.getPopularVideos { popular->
                    _keyword.update {
                        extractSongTitles(popular)
                    }
                Log.e("Video titles:","Chuỗi phổ biến $popular")
            }
        }
    }
    private fun extractSongTitles(keywords: List<String>): MutableList<String> {
        // Tạo danh sách để lưu tên bài hát
        val songTitles = mutableListOf<String>()

        // Duyệt qua từng phần tử trong danh sách keywords
        for (song in keywords) {
            // Tách tên bài hát trước dấu gạch ngang hoặc dấu phẩy
            val title = song.substringBefore(" - ")
                .substringBefore(",")
                .substringBefore("|")
                .trim()

            // Thêm tên bài hát vào danh sách nếu không rỗng
            if (title.isNotEmpty()) {
                songTitles.add(title)
            }
        }

        return songTitles
    }

    fun resetStatusAdding() {
        _isAddingSuccessful.update {
            -1
        }
    }
    fun savePlayCount(userID: String,songID: String){
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val lastTime = songRepository.getLastPlayedTime(userID,songID)
            if(currentTime-lastTime>3600000){
                songRepository.savePlayCount(userID,songID).let {
                        bool ->
                    _isSave.update {
                        bool
                    }
                }
            }
            else{
                _isSave.update {
                    false
                }
            }
        }
    }
    fun upsertHistory(userID: String,itemID: String,type: String){
        viewModelScope.launch {
            songRepository.upsertHistory(userID,itemID,type).let{
                s ->
                _isUpsertSuccessful.update {
                    s
                }
            }
        }
    }

    // comment
    fun getComment(song: Song){
        viewModelScope.launch {
            commentRepositoryImpl.getCommentBySong(song.id).let{
                list ->
                _comments.update {
                    list
                }
            }
        }
    }
    fun getChildComment(commentID: String){

    }
    fun writeComment(
        userID: String,
        content: String,
        songID: String,
        parentID: String){
        viewModelScope.launch {
            commentRepositoryImpl.writeComment(userID,content,songID,parentID).let {
                up ->
                _isSuccessfulComment.update {
                    up
                }
            }
        }
    }
    suspend fun getChildComments(commentID: String): List<Comment>? {
        return withContext(Dispatchers.IO) {
            // Gọi đến repository để lấy các comment con
            commentRepositoryImpl.getChildComments(commentID = commentID)
        }
    }
    suspend fun favComment(commentID:String,userID: String): Int{
        return withContext(Dispatchers.IO){
            commentRepositoryImpl.favComment(commentID = commentID,userID)
        }
    }
    suspend fun unFavComment(commentID:String,userID: String): Int{
        return withContext(Dispatchers.IO){
            commentRepositoryImpl.unFavComment(commentID = commentID,userID)
        }
    }
    suspend fun delComment(comment: Comment): Int{
        return withContext(Dispatchers.IO){
            commentRepositoryImpl.delComment(comment.id)
        }
    }
    fun setReplyingStatus(status: Boolean,comment: Comment = Comment()){
        if(status && !comment.id.isNullOrEmpty()){
            _isReplying.update {
                comment
            }
        }else{
            _isReplying.update {
                Comment()
            }
        }
    }
}