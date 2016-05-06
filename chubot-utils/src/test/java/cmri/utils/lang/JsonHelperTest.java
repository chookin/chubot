package cmri.utils.lang;

import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhuyin on 3/19/15.
 */
public class JsonHelperTest extends TestCase {

    @Test
    public void testToJson() throws Exception {
        List<List<String>> items = new ArrayList<>();
        List<String> item = new ArrayList<>();
        item.add("a");
        item.add("b");
        items.add(item);
        items.add(item);
        String json = JsonHelper.toJson(items);
        System.out.println(json);
    }

    @Test
    public void testToJson1() throws Exception {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("test");
        users.add(user);
        users.add(user);
        String json = JsonHelper.toJson(users);
        System.out.println(json);
    }

    @Test
    public void testParseObject() throws Exception {
        // ref: https://github.com/alibaba/fastjson/wiki/Samples-DataBind
        User user = new User();
        user.setId(1L);
        user.setName("test");
        String jsonString = JsonHelper.toJson(user);
        System.out.println(jsonString);

        User parsedUser = JsonHelper.parseObject(jsonString, User.class);
        assertEquals(user, parsedUser);
    }

    public void testParseObject1(){
        Group group = new Group();
        group.setId(1L);
        group.setName("admin");

        User rootUser = new User();
        rootUser.setId(2L);
        rootUser.setName("root");

        User guestUser = new User();
        guestUser.setId(3L);
        guestUser.setName("guest");

        group.addUser(guestUser);
        group.addUser(rootUser);

        String jsonString = JsonHelper.toJson(group);
        System.out.println(jsonString);
        Group parsedGroup = JsonHelper.parseObject(jsonString, Group.class);
        assertEquals(group, parsedGroup);
    }

    // if inner class, then must be static, or else will throw NullPointerException when deserialization.
    static class User {

        private Long   id;
        private String name;
        private Timestamp time = new Timestamp(System.currentTimeMillis());
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Timestamp getTime() {
            return time;
        }

        public void setTime(Timestamp time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", time=" + time +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (id != null ? !id.equals(user.id) : user.id != null) return false;
            if (name != null ? !name.equals(user.name) : user.name != null) return false;
            return !(time != null ? !time.equals(user.time) : user.time != null);

        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (time != null ? time.hashCode() : 0);
            return result;
        }
    }
    static class Group {

        private Long       id;
        private String     name;
        private Map<Long, User> users = new TreeMap<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // warn: if no get method, then users will not be serialized.
        public Map<Long, User> getUsers() {
            return users;
        }

        public void addUser(User user){
            this.users.put(user.id, user);
        }

        public void addUser(Collection<User> users) {
            users.forEach(user -> this.users.put(user.id, user));
        }

        @Override
        public String toString() {
            return "Group{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", users=" + users +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Group)) return false;

            Group group = (Group) o;

            if (id != null ? !id.equals(group.id) : group.id != null) return false;
            if (name != null ? !name.equals(group.name) : group.name != null) return false;
            if (users != null ? !users.equals(group.users) : group.users != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (users != null ? users.hashCode() : 0);
            return result;
        }
    }
}
