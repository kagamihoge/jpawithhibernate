package jpawithhibernate.fetch;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

public class Main {

    public void go() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("defaultPU");
            em = emf.createEntityManager();

            fetchMillionRows(em);
        } finally {
            em.close();
            emf.close();
        }
    }

    private void fetchMillionRows(EntityManager em) {
        Session s = em.unwrap(Session.class);
        StatelessSession stateLessSession = s.getSessionFactory().openStatelessSession();
        
        final String sql ="SELECT column_pk_int, column_vchar FROM million_rows_table";
        SQLQuery q = stateLessSession.createSQLQuery(sql);
        q.setFetchSize(100);

        ScrollableResults scroll = q.scroll(ScrollMode.FORWARD_ONLY);

        int i = 0;
        while (scroll.next()) {
            Object[] row = scroll.get();
            i++;
        }
        System.out.println(i);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new Main().go();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
