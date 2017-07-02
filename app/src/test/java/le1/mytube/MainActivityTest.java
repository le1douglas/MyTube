package le1.mytube;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import le1.mytube.mvpModel.Repository;
import le1.mytube.mvpPresenters.MainActivityPresenter;
import le1.mytube.mvpViews.MainActivityInterface;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    Repository repository;

    @Mock
    MainActivityInterface view;

    @Test
    public void getHandleAudiofocusTrue(){
        Mockito.when(repository.getHandleAudiofocus()).thenReturn(true);
        MainActivityPresenter presenter = new MainActivityPresenter(repository);
        presenter.bind(view);
        presenter.loadSharedPreferences();

        Mockito.verify(view).displayHandleAudioFocus();
        presenter.unbind();
    }

    @Test
    public void getHandleAudiofocusFalse(){
        Mockito.when(repository.getHandleAudiofocus()).thenReturn(false);
        MainActivityPresenter presenter = new MainActivityPresenter(repository);
        presenter.bind(view);
        presenter.loadSharedPreferences();

        Mockito.verify(view).displayNoHandleAudioFocus();
        presenter.unbind();
    }


    @Test
    public void getAllPlaylistSomePlalist(){
        ArrayList<String> list= new ArrayList<>();
        list.add("PLACEHOLDER1");
        list.add("PLACEHOLDER2");
        list.add("PLACEHOLDER3");
        Mockito.when(repository.getAllPlaylistsName()).thenReturn(list);
        MainActivityPresenter presenter = new MainActivityPresenter(repository);
        presenter.bind(view);
        presenter.loadPlaylists();

        Mockito.verify(view).displayPlaylist(list);
        presenter.unbind();
    }

    @Test
    public void getAllPlaylistNoPlalist(){
        ArrayList<String> list= new ArrayList<>();

        Mockito.when(repository.getAllPlaylistsName()).thenReturn(list);
        MainActivityPresenter presenter = new MainActivityPresenter(repository);
        presenter.bind(view);
        presenter.loadPlaylists();

        Mockito.verify(view).displayNoPlaylist();
        presenter.unbind();
    }

    @Test
    public void getAllPlaylistError(){

        Mockito.when(repository.getAllPlaylistsName()).thenThrow(new RuntimeException("error"));
        MainActivityPresenter presenter = new MainActivityPresenter(repository);
        presenter.bind(view);
        presenter.loadPlaylists();

        Mockito.verify(view).displayErrorPlaylist();
        presenter.unbind();
    }

}
