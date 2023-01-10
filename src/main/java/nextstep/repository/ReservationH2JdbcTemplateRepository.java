package nextstep.repository;

import nextstep.Reservation;
import nextstep.exception.ReservationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public class ReservationH2JdbcTemplateRepository implements ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReservationH2JdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Reservation add(Reservation reservation) {
        String sql = "INSERT INTO reservation (date, time, name, theme_name, theme_desc, theme_price) VALUES (?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setDate(1, Date.valueOf(reservation.getDate()));
            ps.setTime(2, Time.valueOf(reservation.getTime()));
            ps.setString(3, reservation.getName());
            ps.setString(4, reservation.getTheme().getName());
            ps.setString(5, reservation.getTheme().getDesc());
            ps.setInt(6, reservation.getTheme().getPrice());
            return ps;
        }, keyHolder);

        reservation.setId(keyHolder.getKey().longValue());
        return reservation;
    }

    @Override
    public Reservation get(Long id)  throws ReservationNotFoundException {
        String sql = "SELECT * FROM reservation where id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> Reservation.fromResultSet(resultSet),
                    id);
        } catch (EmptyResultDataAccessException e) {
            throw new ReservationNotFoundException();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reservation where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean hasReservationAt(LocalDate date, LocalTime time) {
        String sql = "SELECT count(*) AS cnt FROM reservation WHERE date = ? AND time = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, Date.valueOf(date), Time.valueOf(time));
        return count >= 1;
    }
}