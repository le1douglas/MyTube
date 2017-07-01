package le1.mytube;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import le1.mytube.mvpModel.Model;
import le1.mytube.mvpPresenters.PlaylistPresenter;
import le1.mytube.mvpViews.PlaylistInterface;

import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;

/**
 * Created by Leone on 30/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlaylistTest {

    @Mock
    PlaylistInterface view;
    @Mock
    Model model;

    @Test
    public void getSongsInPlaylistSomeSongs() {
        ArrayList<YouTubeSong> songs = new ArrayList<YouTubeSong>();
        songs.add(new YouTubeSong(null, null, null, null, null));
        songs.add(new YouTubeSong(null, null, null, null, null));
        songs.add(new YouTubeSong(null, null, null, null, null));

        Mockito.when(model.getSongsInPlaylist("PLACEHOLDER")).thenReturn(songs);

        PlaylistPresenter presenter = new PlaylistPresenter(model);
        presenter.bind(view);
        presenter.loadSongsInPlaylist("PLACEHOLDER");

        Mockito.verify(view).displaySongs(songs);
        presenter.unbind();

    }

    @Test
    public void getSongsInPlaylistNoSongs() {
        ArrayList<YouTubeSong> songs = new ArrayList<>();

        Mockito.when(model.getSongsInPlaylist("PLACEHOLDER")).thenReturn(songs);

        PlaylistPresenter presenter = new PlaylistPresenter(model);
        presenter.bind(view);
        presenter.loadSongsInPlaylist("PLACEHOLDER");

        Mockito.verify(view).displayNoSongs();
        presenter.unbind();

    }

    @Test
    public void getSongsInPlaylistError() {
        Mockito.when(model.getSongsInPlaylist("PLACEHOLDER")).thenThrow(new RuntimeException("something went wrong (sql exception)"));

        PlaylistPresenter presenter = new PlaylistPresenter(model);
        presenter.bind(view);
        presenter.loadSongsInPlaylist("PLACEHOLDER");

        Mockito.verify(view).displayError();
        presenter.unbind();

    }

    @Test
    public void getAllSongsSomeSongs(){
        ArrayList<YouTubeSong> songs = new ArrayList<>();
        songs.add(new YouTubeSong(null, null, null, null, null));
        songs.add(new YouTubeSong(null, null, null, null, null));
        songs.add(new YouTubeSong(null, null, null, null, null));
        Mockito.when(model.getAllSongs()).thenReturn(songs);

        PlaylistPresenter presenter = new PlaylistPresenter(model);
        presenter.bind(view);
        presenter.loadSongsInPlaylist(TB_NAME);

        Mockito.verify(view).displaySongs(songs);
        presenter.unbind();

    }

    @Test
    public void getAllSongsNoSongs(){
        Mockito.when(model.getAllSongs()).thenReturn(new ArrayList<YouTubeSong>());

        PlaylistPresenter presenter = new PlaylistPresenter(model);
        presenter.bind(view);
        presenter.loadSongsInPlaylist(TB_NAME);

        Mockito.verify(view).displayNoSongs();
        presenter.unbind();

    }

}
