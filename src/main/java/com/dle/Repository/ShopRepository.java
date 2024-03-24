package com.dle.Repository;

import com.dle.bean.database.ShopInfo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShopRepository implements PanacheRepository<ShopInfo> {
}
