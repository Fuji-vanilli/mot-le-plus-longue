package org.dcdl.repository;

import org.dcdl.models.Word;
import org.dcdl.exception.DatabaseOperationException;
import org.dcdl.utils.SingletonConnection;
import org.dcdl.validator.WordValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EntityRepositoryImplTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private WordValidator wordValidator;
    @InjectMocks
    private WordRepositoryImpl wordRepository;
    private AutoCloseable autoCloseable;
    private MockedStatic<SingletonConnection> singletonConnectionMockedStatic;

    @Before
    public void setUp() throws Exception {
        autoCloseable= MockitoAnnotations.openMocks(this);
        singletonConnectionMockedStatic= mockStatic(SingletonConnection.class);
        when(SingletonConnection.getConnection()).thenReturn(connection);

        wordRepository= new WordRepositoryImpl();
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
        singletonConnectionMockedStatic.close();
    }

    @Test
    public void testExistByName_WordExists_ShouldReturnTrue() throws SQLException {
        final String name= "testName";

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean isExist = wordRepository.existByName(name);

        assertThat(isExist).isTrue();

        verify(preparedStatement, times(1)).setString(1, name);
        verify(preparedStatement, times(1)).executeQuery();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testExistByName_WordExists_ShouldReturnFalse() throws SQLException {
        final String name= "testName";

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean isExist = wordRepository.existByName(name);

        assertThat(isExist).isFalse();

        verify(preparedStatement, times(1)).setString(1, name);
        verify(preparedStatement, times(1)).executeQuery();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testSave_InvalidWord_ShouldReturnNull() throws SQLException {
        final Word word= new Word("w");
        when(wordValidator.validate(any(Word.class))).thenReturn(false);

        Word wordAdded = wordRepository.save(word);

        assertThat(wordAdded).isNull();

        //verify(wordValidator, times(1)).validate(word);
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test(expected = DatabaseOperationException.class)
    public void testSave_SQLException_ShouldThrownException() throws SQLException {
        final Word word= new Word("wordToAdd");

        when(wordValidator.validate(any(Word.class))).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        Word wordAdded = wordRepository.save(word);

        assertThat(wordAdded).isNull();
    }

    @Test
    public void testAddWord_ShouldOperationSuccessfully() throws SQLException {
        final Word word= new Word("wordToAdd");

        when(wordValidator.validate(any(Word.class))).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Word wordAdded = wordRepository.save(word);

        assertThat(wordAdded).isNotNull();
        assertThat(word.getName()).isEqualTo(wordAdded.getName());
        assertThat(word.getSize()).isEqualTo(wordAdded.getSize());

        verify(connection, times(1)).setAutoCommit(false);
        verify(preparedStatement, times(1)).setString(1, word.getName());
        verify(preparedStatement, times(1)).setInt(2, 9);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(connection, times(1)).commit();
        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).setAutoCommit(true);
    }







}