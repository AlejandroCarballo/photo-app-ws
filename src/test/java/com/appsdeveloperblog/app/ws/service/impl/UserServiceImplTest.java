package com.appsdeveloperblog.app.ws.service.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;

public class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	String userId = "hhttp57ehfy";

	String encrypetPassword = "74hgh8474jf";

	UserEntity userEntity;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Alejandro");
		userEntity.setLastName("Carballo");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encrypetPassword);
		userEntity.setEmail("test@tes.com");
		userEntity.setEmailVerificationToken("dasdadevsdfds");
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	public void testGetUserString() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("test2@test.com");

		assertNotNull(userDto);
		assertEquals("Alejandro", userDto.getFirstName());
	}

	@Test
	public void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		

	}
	
	@Test
	final void testCreateUser_CreateUserServiceException() {
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Ale");
		userDto.setLastName("Carballo");
		userDto.setPassword("123456");
		userDto.setEmail("Ale@ale.com");
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		assertThrows(UserServiceException.class, () -> {
			userService.createUser(userDto);
		});
		
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);

		when(utils.generateAddressId(anyInt())).thenReturn("32167asdas381");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(userId);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setType("shipping");
		addressDTO.setCity("BS AS");
		addressDTO.setCountry("Peru");
		addressDTO.setStreetName("Public 11");
		addressDTO.setPostalCode("5000");

		AddressDTO billingAddressDTO = new AddressDTO();
		billingAddressDTO.setType("shipping");
		billingAddressDTO.setCity("BS AS");
		billingAddressDTO.setCountry("Peru");
		billingAddressDTO.setStreetName("Public 11");
		billingAddressDTO.setPostalCode("5000");

		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDTO);
		addresses.add(billingAddressDTO);

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Ale");
		userDto.setLastName("Carballo");
		userDto.setPassword("123456");
		userDto.setEmail("Ale@ale.com");

		UserDto storedUserDetails = userService.createUser(userDto);
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(),userEntity.getAddresses().size());
		verify(utils,times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("12345678");
		verify(userRepository, times(1)).save(any(UserEntity.class));

	}
	
	private List<AddressDTO> getAddressesDto(){
		
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setType("shipping");
		addressDTO.setCity("BS AS");
		addressDTO.setCountry("Peru");
		addressDTO.setStreetName("Public 11");
		addressDTO.setPostalCode("5000");

		AddressDTO billingAddressDTO = new AddressDTO();
		billingAddressDTO.setType("shipping");
		billingAddressDTO.setCity("BS AS");
		billingAddressDTO.setCountry("Peru");
		billingAddressDTO.setStreetName("Public 11");
		billingAddressDTO.setPostalCode("5000");

		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDTO);
		addresses.add(billingAddressDTO);
		
		return addresses;

		
	}
	
	private List<AddressEntity> getAddressesEntity(){
		
		List<AddressDTO> addresses =  getAddressesDto();
		
		Type listType = new TypeToken<List<AddressesRest>>() {
		}.getType();
		
		return new ModelMapper.map(addresses, listType);
		
		
		
	}

}
