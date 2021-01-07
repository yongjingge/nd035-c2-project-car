package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository carRepository;
    private final MapsClient mapsClient;
    private final PriceClient priceClient;

    public CarService(CarRepository carRepository, MapsClient mapsClient, PriceClient priceClient) {
        this.carRepository = carRepository;
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {

        List<Car> res = carRepository.findAll();
        for (Car carItem : res) {
            if (carItem.getPrice() == null) {
                String price = priceClient.getPrice(carItem.getId());
                carItem.setPrice(price);
            }
            if (carItem.getLocation().getAddress() == null) {
                Location location = carItem.getLocation();
                location = mapsClient.getAddress(location);
                carItem.setLocation(location);
            }
        }

        return res;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         *
         * DONE
         */
        Car car = carRepository.findById(id).get();
        if (car.getId() == null) {
            throw new CarNotFoundException();
        }

        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         *
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         *
         * DONE
         */
        String price = priceClient.getPrice(id);
        car.setPrice(price);

        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         *
         * DONE
         */
        Location location = car.getLocation();
        Location addressOfLocation = mapsClient.getAddress(location);
        car.setLocation(addressOfLocation);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            // update the vehicle by its condition, details, and location, also need to reflect its modified time
            return carRepository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setModifiedAt(LocalDateTime.now());
                        return carRepository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return carRepository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *
         * DONE
         */
        Car targetCar = carRepository.findById(id).orElseThrow(CarNotFoundException::new);

        /**
         * TODO: Delete the car from the repository.
         *
         * DONE
         */
        carRepository.delete(targetCar);

    }
}
