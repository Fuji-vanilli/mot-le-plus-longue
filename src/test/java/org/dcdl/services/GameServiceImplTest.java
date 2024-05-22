package org.dcdl.services;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GameServiceImplTest{
    @Mock
    private GameServiceImpl gameService;
    private AutoCloseable autoCloseable;

    @Before
    public void setUp() throws Exception {
        autoCloseable= MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testMatcher_ShouldCheckTrue() {
        when(gameService.checkMatcher(anyString(), anyString())).thenReturn(true);

        boolean matched = gameService.checkMatcher("anjoubr", "bon");

        assertThat(matched).isTrue();
    }

    @Test
    public void testMatcher_ShouldCheckFalse() {
        when(gameService.checkMatcher(anyString(), anyString())).thenReturn(false);

        boolean matched = gameService.checkMatcher("anjoubre", "banane");

        assertThat(matched).isFalse();
    }

}