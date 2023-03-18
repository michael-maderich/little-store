package com.littlestore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.littlestore.entity.Role;

// Handles Security Configuration. Pages and Requests must have valid permissions to load/function

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Qualifier("userDetailsServiceImpl")
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Admin pages (manage inventory/customers/orders etc
		http.csrf().disable().authorizeRequests()
			.antMatchers(HttpMethod.DELETE, "/admin/products/{productId}").hasAnyRole(Role.Roles.ADMIN.name(), Role.Roles.OWNER.name()) 	// Owner should be able to delete
			.antMatchers(HttpMethod.PUT, "/admin/products/{productId}").hasAnyRole(Role.Roles.ADMIN.name(), Role.Roles.OWNER.name()) 		// Owner should be able to update
			.antMatchers("/admin/products/add").hasAnyRole(Role.Roles.ADMIN.name(), Role.Roles.OWNER.name()) // Admin and Supervisor should be able to add product.
			.antMatchers("/admin/**").hasAnyRole(Role.Roles.ADMIN.name(), Role.Roles.OWNER.name())
			.antMatchers("/admin").hasAnyRole(Role.Roles.ADMIN.name(), Role.Roles.OWNER.name())
			.antMatchers("/**").permitAll()
			.and()
			.formLogin()
				.loginPage("/login").permitAll()
				.and()
			.logout().permitAll()
			.and()
			.httpBasic();

/*			.antMatchers("/**").hasRole("USER").and().formLogin();
			.antMatchers("/**").permitAll()
			.and().formLogin().loginPage("/login").failureUrl("/login?error=Invalid%20credentials").permitAll();
/*				.antMatchers("/", "index", "login", "signup", "cart").permitAll()
//				.antMatchers("inventoryManager", "customerManager").hasAnyRole("OWNER", "ADMIN")
				.antMatchers("account").fullyAuthenticated()
//				.anyRequest().authenticated()
				.and()
			//.csrf().disable()		// cross-site request forgery protection (disabled?)
			.formLogin().loginPage("/login").permitAll()
				//.failureUrl("/login?error=true")
				//.loginProcessingUrl("/processlogin").successfulHandler(customAuthenticationSuccessHandler)
				.defaultSuccessUrl("/")
				//.usernameParameter("userName") //email?
				//.passwordParameter("password")
				.permitAll()
				.and()
			.logout().logoutUrl("/logout");
				.logoutSuccessUrl("/index")
			//	.logoutSuccessHandler(logoutSuccessHandler) 
				.invalidateHttpSession(true)
			//	.addLogoutHandler(logoutHandler)
			//	.deleteCookies(cookieNamesToClear)
			//.logout()	//.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			//.logoutUrl("/")//("/logout")
			//.logoutSuccessUrl("/index")
				.permitAll();
		//.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			//.logoutSuccessUrl("/")
			//.and().rememberMe()
			//.tokenValiditySeconds(60*60)
			//.and().exceptionHandling().accessDeniedPage("/access-denied")
        http.authorizeRequests().antMatchers(HttpMethod.GET).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST).denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/you/can/alsoSpecifyAPath").denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/path/is/Case/Insensitive").denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/and/can/haveWildcards/*").denyAll();*/
	}
	
	// Allow unrestricted access to resources, stylesheets, scripts, images, etc
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/styles/**", "/scripts/**", "/css/**", "/js/**", "/images/**");
	}
	
	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		return authenticationManager();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
}
