package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = createCarObjectForTesting();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = createCarObjectForTesting();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }


    /**
     * Test for successful update of an existing car in the system
     * @throws Exception when car update fails in the system
     */
    @Test
    public void updateCar () throws Exception {
        Car car = createCarObjectForTesting();
        car = carService.save(car);
        Long carId = car.getId();

        // update info
        Car updateCarInfo = new Car();
        updateCarInfo.setId(carId);
        updateCarInfo.setLocation(updateCarLocation());
        updateCarInfo.setDetails(updateCarDetails());
        updateCarInfo.setCondition(Condition.NEW);

        mvc.perform(
                put("/cars/" + carId)
                .content(json.write(updateCarInfo).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is2xxSuccessful());
    }


    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   the whole list of vehicles. This should utilize the car from `getCar()`
         *   below (the vehicle will be the first in the list).
         *
         * DONE
         */

        Car firstCar = createCarObjectForTesting();
        firstCar = carService.save(firstCar);
        List<Car> carList = carService.list();
        Long firstCarInListId = carList.get(0).getId();
        assertEquals(firstCar.getId(), firstCarInListId);
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   a vehicle by ID. This should utilize the car from `getCar()` below.
         *
         * DONE
         */

        Car targetCar = createCarObjectForTesting();
        targetCar = carService.save(targetCar);
        Car getCar = carService.findById(targetCar.getId());
        assertEquals(targetCar, getCar);

        mvc.perform(
                get("/cars/" + targetCar.getId())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        verify(carService, times(2)).findById(targetCar.getId());
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        /**
         * TODO: Add a test to check whether a vehicle is appropriately deleted
         *   when the `delete` method is called from the Car Controller. This
         *   should utilize the car from `getCar()` below.
         *
         * DONE
         */

        Car targetCar = createCarObjectForTesting();
        targetCar = carService.save(targetCar);
        Long id = targetCar.getId();
        mvc.perform(delete("/cars/" + id)
            .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).delete(id);
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car createCarObjectForTesting() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

    /**
     * Provides an example Location object
     * @return Location
     */
    private Location updateCarLocation () {
        Location locationUpdate = new Location(40.730610, -73.935242);
        locationUpdate = mapsClient.getAddress(locationUpdate);

//        locationUpdate.setAddress("1 Somewhere St");
//        locationUpdate.setCity("Toronto");
//        locationUpdate.setState("Ontario");
//        locationUpdate.setZip("N8Q 1E1");
        return locationUpdate;
    }

    /**
     * Provides an example Details object
     * @return Details
     */
    private Details updateCarDetails () {
        Details detailsUpdate = new Details();
        detailsUpdate.setModel("Impala");
        detailsUpdate.setMileage(22020);
        detailsUpdate.setExternalColor("black");
        detailsUpdate.setBody("sedan");
        detailsUpdate.setEngine("4.0L V6");
        detailsUpdate.setFuelType("Gasoline");
        detailsUpdate.setModelYear(2020);
        detailsUpdate.setProductionYear(2020);
        detailsUpdate.setNumberOfDoors(2);
        detailsUpdate.setManufacturer(new Manufacturer(102, "Ford"));

        return detailsUpdate;
    }
}