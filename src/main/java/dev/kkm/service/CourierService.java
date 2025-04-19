package dev.kkm.service;



import dev.kkm.model.CourierDetail;

import java.util.List;

/**
 * @implNote couriers manager
 * @version 1.0.0
 * @since  18/04/2025
 * @author <a href="mailto:maximiliendenver@gmail.com">maximilien kengne kongne</a>
 */
public interface CourierService {
    void sendCourier(CourierDetail courierDetail);
    void sendCourier(List<CourierDetail> courierDetails);
}
