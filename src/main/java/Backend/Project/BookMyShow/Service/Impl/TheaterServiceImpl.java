package Backend.Project.BookMyShow.Service.Impl;

import Backend.Project.BookMyShow.Converter.TheaterConverter;
import Backend.Project.BookMyShow.Dto.EntryRequestDto.TheaterEntryDto;
import Backend.Project.BookMyShow.Dto.ResponseDto.TheaterResponseDto;
import Backend.Project.BookMyShow.Model.MovieEntity;
import Backend.Project.BookMyShow.Model.ShowEntity;
import Backend.Project.BookMyShow.Model.TheaterEntity;
import Backend.Project.BookMyShow.Model.TheaterSeatEntity;
import Backend.Project.BookMyShow.Repository.MovieRepository;
import Backend.Project.BookMyShow.Repository.TheaterRepository;
import Backend.Project.BookMyShow.Repository.TheaterSeatRepository;
import Backend.Project.BookMyShow.Service.TheaterService;
import Backend.Project.BookMyShow.enums.SeatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
@Transactional
public class TheaterServiceImpl implements TheaterService {
    @Autowired
    TheaterRepository theaterRepository;
    @Autowired
    TheaterSeatRepository theaterSeatRepository;

    @Autowired
    MovieRepository movieRepository;

    @Override
    public TheaterResponseDto addTheater(TheaterEntryDto theaterEntryDto) {
        // we need a thater entity
        TheaterEntity theaterEntity = TheaterConverter.convertDtoToEntity(theaterEntryDto);

        // create theater Seats
        List<TheaterSeatEntity> seats = createTheaterSeats();
        for (TheaterSeatEntity theaterSeatEntity : seats) {
            theaterSeatEntity.setTheater(theaterEntity);
        }

        theaterRepository.save(theaterEntity);

        return TheaterConverter.convertEntityToDto(theaterEntity);
    }

    List<TheaterSeatEntity> createTheaterSeats() {
        List<TheaterSeatEntity> seats = new ArrayList<>();

        seats.add(getTheaterSeat("1A", 100, SeatType.CLASSIC));
        seats.add(getTheaterSeat("1B", 100, SeatType.CLASSIC));
        seats.add(getTheaterSeat("1C", 100, SeatType.CLASSIC));
        seats.add(getTheaterSeat("1D", 100, SeatType.CLASSIC));
        seats.add(getTheaterSeat("1E", 100, SeatType.CLASSIC));

        seats.add(getTheaterSeat("2A", 100, SeatType.PREMIUM));
        seats.add(getTheaterSeat("2B", 100, SeatType.PREMIUM));
        seats.add(getTheaterSeat("2C", 100, SeatType.PREMIUM));
        seats.add(getTheaterSeat("2D", 100, SeatType.PREMIUM));
        seats.add(getTheaterSeat("2E", 100, SeatType.PREMIUM));

        theaterSeatRepository.saveAll(seats);

        return seats;
    }

    TheaterSeatEntity getTheaterSeat(String seatNumber, int rate, SeatType seatType) {
        return TheaterSeatEntity.builder().seatNumber(seatNumber).rate(rate).seatType(seatType).build();
    }

    @Override
    public TheaterResponseDto getTheater(int id) {
        TheaterEntity theaterEntity = theaterRepository.findById(id).get();
        TheaterResponseDto theaterResponseDto = TheaterConverter.convertEntityToDto(theaterEntity);
        return theaterResponseDto;
    }

    public List<TheaterEntity> getTheatresByMovieShowDateTime(String movieName, LocalDate showDate, LocalTime showtime) {

        MovieEntity movieEntity = movieRepository.findByName(movieName);
        List<TheaterEntity> theaterEntityList = movieEntity.getTheaterEntityList();
       return theaterEntityList.stream().filter(
                theaterEntity -> theaterEntity.getShowList().stream().filter(showEntity -> {
                    return showDate.equals(showEntity.getShowDate()) && showtime.equals(showEntity.getShowTime());
                }).isParallel()).collect(Collectors.toList());

        /*List<ShowEntity> showEntityList = movieEntity.getShowsList();
        List<ShowEntity> showEntities = showEntityList.stream().
                filter(showEntity -> showDate.equals(showEntity.getShowDate()) && showtime.equals(showEntity.getShowTime())).collect(Collectors.toList());

        List<TheaterEntity> theaterEntityList = showEntities.stream().map(showEntity -> showEntity.getTheater()).collect(Collectors.toList());

        return theaterEntityList;*/
    }
}
