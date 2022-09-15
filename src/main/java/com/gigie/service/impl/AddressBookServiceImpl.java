package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.AddressBook;
import com.gigie.mapper.AddressBookMapper;
import com.gigie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
