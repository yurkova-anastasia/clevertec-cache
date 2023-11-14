package lfuCache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.cache.cache.lfuCache.LFUCache;
import ru.clevertec.cache.model.User;

import java.time.LocalDate;
import java.util.List;

class LFUCacheTest {

    private LFUCache cache = new LFUCache(3);

    @BeforeEach
    void setUp() {
        cache = new LFUCache(3);
        User user1 = new User("Name1", "Surname1", 20,
                LocalDate.of(2023, 12, 12), true);
        user1.setId(1L);
        User user2 = new User("Name2", "Surname2", 20,
                LocalDate.of(2023, 12, 12), true);
        user2.setId(2L);
        cache.put(1L, user1);
        cache.put(2L, user2);
    }

    @Test
    void checkGetShouldReturnEntityWithId1() {
        //given
        User expected = new User("Name1", "Surname1", 20,
                LocalDate.of(2023, 12, 12), true);
        expected.setId(1L);
        //when
        Object actual = cache.get(1L);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkGetAllShouldReturnEntitiesWithIds1And2() {
        //given
        User user1 = new User("Name1", "Surname1", 20,
                LocalDate.of(2023, 12, 12), true);
        user1.setId(1L);
        User user2 = new User("Name2", "Surname2", 20,
                LocalDate.of(2023, 12, 12), true);
        user2.setId(2L);
        List<User> expected = List.of(user1, user2);

        //when
        List<Object> actual = cache.getAll();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkSetShouldAddThirdEntityToCache() {
        //given
        User user1 = new User("Name1", "Surname1", 20,
                LocalDate.of(2023, 12, 12), true);
        user1.setId(1L);
        User user2 = new User("Name2", "Surname2", 20,
                LocalDate.of(2023, 12, 12), true);
        user2.setId(2L);
        User user3 = new User("Name3", "Surname3", 20,
                LocalDate.of(2023, 12, 12), true);
        user3.setId(3L);
        List<User> expected = List.of(user1, user2, user3);

        //when
        cache.put(3L, user3);
        List<Object> actual = cache.getAll();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkSetShouldRemoveEntityWithId2ToGetCapacityForNewOneAndAddNewEntity() {
        //given
        User user1 = new User("Name1", "Surname1", 20,
                LocalDate.of(2023, 12, 12), true);
        user1.setId(1L);
        User user2 = new User("Name2", "Surname2", 20,
                LocalDate.of(2023, 12, 12), true);
        user2.setId(2L);
        User user3 = new User("Name3", "Surname3", 20,
                LocalDate.of(2023, 12, 12), true);
        user3.setId(3L);
        User user4 = new User("Name4", "Surname4", 20,
                LocalDate.of(2023, 12, 12), true);
        user3.setId(4L);

        List<User> expected = List.of(user4, user1, user3);
        cache.put(3L, user3);
        cache.get(1L);
        cache.get(3L);

        //when
        cache.put(4L, user4);
        List<Object> actual = cache.getAll();

        //then
        Assertions.assertEquals(expected, actual);

    }

    @Test
    void checkDeleteShouldDeleteEntityFromCache() {
        //given
        int sizeBeforeDelete = cache.getAll().size();

        //when
        cache.delete(1L);
        int sizeAfterDelete = cache.getAll().size();

        //then
        Assertions.assertNotEquals(sizeBeforeDelete, sizeAfterDelete);
    }
}