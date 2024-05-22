package org.dcdl.repository;

import lombok.extern.slf4j.Slf4j;
import org.dcdl.models.Word;
import org.dcdl.exception.DatabaseOperationException;
import org.dcdl.utils.SingletonConnection;
import org.dcdl.validator.WordValidator;
import org.dcdl.validator.WordValidatorImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class WordRepositoryImpl implements WordRepository {
    public static Long NUMBER_OF_WORD= 0L;
    public static final Lock lock= new ReentrantLock();
    private final Connection connection;
    private final WordValidator wordValidator;
    public WordRepositoryImpl() {
        connection= SingletonConnection.getConnection();
        wordValidator= new WordValidatorImpl();
        initWordCount();
    }

    public void initWordCount() {
        if (NUMBER_OF_WORD== 0L) {
            NUMBER_OF_WORD= getWordCount();
        }
    }
    @Override
    public Word save(Word word) {
        boolean isValid= wordValidator.validate(word);
        if (!isValid) {
            log.error("word not valid to insert into the database!");
            return null;
        }

        PreparedStatement preparedStatement= null;
        boolean isSuccess= false;
        Word newWord= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = loadPreparedStatement(
                    "INSERT INTO word(name, size) VALUES (?, ?) ",
                    "error to add a new word into the database");
            
            preparedStatement.setString(1, word.getName());
            preparedStatement.setInt(2, word.getName().length());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected> 0) {
                isSuccess= true;
                newWord= word;
            }

            connection.commit();

            incrementNumberWord();
            log.info("new word added successfully into the database!");

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollBackException) {
                throw new DatabaseOperationException("error to abort the transaction", rollBackException);
            }
            throw new DatabaseOperationException("error to insert values into the database!", e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close the preparedStatement");
                }
            }

            if (!Objects.isNull(connection)) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    log.error("error to close the connection");
                }
            }
        }

        return isSuccess? newWord: null;
    }

    @Override
    public Word findById(Long id) {
        PreparedStatement preparedStatement= null;
        Word word= null;

        try {
            preparedStatement= loadPreparedStatement(
                    "SELECT * FROM word WHERE id like ?",
                    "error to connect and find word by id"
            );
            preparedStatement.setLong(1, id);
            ResultSet result= preparedStatement.executeQuery();

            if (result.next()) {
                word= Word.builder()
                        .id(result.getLong("id"))
                        .name(result.getString("name"))
                        .size(result.getInt("size"))
                        .build();
            }

            log.info("word with the id: {} getted successfully!", id);

        } catch (SQLException e) {
            throw new DatabaseOperationException("error to connect into the database", e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close the preparedstatement");
                }
            }
        }

        return word;
    }

    @Override
    public List<Word> findAll() {
        PreparedStatement preparedStatement= null;
        List<Word> words= new ArrayList<>();

        try {
            preparedStatement= loadPreparedStatement(
                    "SELECT * FROM word",
                    "error to load preparedStatement for find all word"
            );

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                words.add(Word.builder()
                        .id(resultSet.getLong("ID"))
                        .name(resultSet.getString("NAME"))
                        .size(resultSet.getInt("SIZE"))
                        .build()
                );
            }

            log.info("all word finding successfully!");

        } catch (SQLException e) {
            throw new DatabaseOperationException("error fo find all word from database", e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close preparedStatement for the finding all word!");
                }
            }
        }

        return words;
    }

    @Override
    public boolean existByName(String name) {
        PreparedStatement preparedStatement= null;
        try {
            preparedStatement= loadPreparedStatement(
                    "SELECT 1 FROM word WHERE name= ?",
                    "error to load preparedStatement for testing if word is exist!"
            );

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close preparedStatement for testing if word exist!");
                }
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        PreparedStatement preparedStatement= null;

        try {
            preparedStatement= loadPreparedStatement(
                    "DELETE FROM word WHERE id= ? ",
                    "error to load preparedStatement for deleting word"
            );

            preparedStatement.setLong(1, id);
            int rowForDeleted = preparedStatement.executeUpdate();

            if (rowForDeleted== 0) {
                log.warn("no word for deleted!");
            }


        } catch (SQLException e) {
            throw new DatabaseOperationException("error to delete the word!", e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close preparedStatement for deleting word!");
                }
            }
        }
    }
    private long getWordCount() {
        long count= 0L;
        PreparedStatement preparedStatement= null;

        try {
            preparedStatement= loadPreparedStatement(
                    "SELECT MAX(id) as MAX_ID FROM word",
                    "error to load preparedStatement word from database!"
            );
            ResultSet result= preparedStatement.executeQuery();
            if (result.next()) {
                count= result.getLong("MAX_ID");
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("error to find total count of word from the database!", e);
        } finally {
            if (!Objects.isNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("error to close the preparedStatement!");
                }
            }
        }

        return count;
    }
    private void incrementNumberWord() {
        lock.lock();
        try {
            NUMBER_OF_WORD++;
        } finally {
            lock.unlock();
        }
    }
    private PreparedStatement loadPreparedStatement(String request, String messageException) {
        PreparedStatement preparedStatement;

        try {
            preparedStatement= connection.prepareStatement(
                    request
            );
        } catch (SQLException e) {
            throw new DatabaseOperationException(messageException, e);
        }

        return preparedStatement;
    }

    @Override
    public long totalWords() {
        return NUMBER_OF_WORD;
    }
}
