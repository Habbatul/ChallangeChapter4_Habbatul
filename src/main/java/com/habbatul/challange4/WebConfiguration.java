package com.habbatul.challange4;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//konfigurasi spring mvc untuk melakukan rewrite root ke swagger-ui/index.html

@EnableAsync
@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // Set the allowed origin pattern
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }



    //task eksekutor untuk async
    @Bean(name = "asyncTaskExecutor")
    public TaskExecutor asyncTaskExecutor() {
//        return new SimpleAsyncTaskExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); //jumlah core/inti dari thread yang tetap ada dalam pool
        executor.setMaxPoolSize(10); //jumlah maksimum thread dalam pool
        executor.setQueueCapacity(50); //kapasitas antrean yang digunakan jika jumlah thread lebih dari coreThread
        executor.setThreadNamePrefix("QolbiThread-"); //nama thread
        executor.initialize();
        return executor;
    }


    //melakukan redirect ke root ke swagger open api
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/swagger-ui/index.html");
    }


    //melakukan custom title ddan deskripsi
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("BinarFud OPEN API")
                        .description("<b>Ketika POST /auth/signin maka akan menggenerate cookies, sehingga " +
                                "langsung bisa hit endpoint yang diinginkan sesuai hak aksesnya, tapi bisa " +
                                "juga menggunakan authentication dengan skema openapi dengan menekan tombol " +
                                "Authorize (Bisa dicoba dengan menghapus cookies pada browser terlebih dahulu).</b><br><br>" +
                                "Rest API ini dibuat dalam rangka menyelesaikan challenge " +
                                "chapter 5. Selain kembalian dalam bentuk JSON, terdapat juga fitur " +
                                "endpoint untuk menghasilkan bentuk file PDF yang estetik dengan Jasper." +
                                " Pastikan untuk menambahkan (POST) data " +
                                "sebelum mengirimkan method GET, PUT & DELETE." +
                                "<br><br> <b>CUSTOMER</b> : <br>" +
                                "PUT" +
                                "/user<br>" +
//                                "POST" +
//                                "/user (sementara tidak saya hapus)<br>" +
                                "DELETE" +
                                "/user<br>" +
                                "GET" +
                                "/order<br>" +
                                "POST" +
                                "/order<br>" +
                                "POST" +
                                "/order/print<br>" +
                                "GET" +
                                "/order/admin (sementara tidak saya hapus)<br>" +
                                "<br><b>MERCHANT:</b><br>" +
                                "PUT" +
                                "/product/{productCode}<br>" +
                                "DELETE" +
                                "/product/{productCode}<br>" +
                                "POST" +
                                "/product<br>" +
                                "PUT" +
                                "/merchant/{merchantName}<br>" +
                                "POST" +
                                "/merchant<br>" +
                                "<br><b>ALL PERMIT : </b> <br>" +
                                "GET" +
                                "/product<br>" +
                                "GET" +
                                "/merchant<br>" +
                                "POST" +
                                "/auth/signup<br>" +
                                "POST" +
                                "/auth/signin" +
                                "<br><br>Kunjungi beberapa URL di bawah ini untuk portofolio/proyek saya lainnya:" +
                                "<ul>" +
                                "<li>[Portofolio Website](https://hq.achmodez.tech)</li>" +
                                "<li>[Linkedin Profile](https://www.linkedin.com/in/habbatul/)</li>" +
                                "<ul>")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
