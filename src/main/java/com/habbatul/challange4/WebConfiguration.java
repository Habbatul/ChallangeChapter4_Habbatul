package com.habbatul.challange4;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//konfigurasi spring mvc untuk melakukan rewrite root ke swagger-ui/index.html
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    //melakukan redirect ke root ke swagger open api
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/swagger-ui/index.html");
    }

    //melakukan custom title ddan deskripsi
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BinarFud OPEN API")
                        .description("Rest API ini dibuat dalam rangka menyelesaikan challenge " +
                                "chapter 5. Selain kembalian dalam bentuk JSON, terdapat juga fitur " +
                                "endpoint untuk menghasilkan bentuk file PDF yang estetik dengan Jasper." +
                                " Pastikan untuk menambahkan (POST) data " +
                                "sebelum mengirimkan method GET, PUT & DELETE." +
                                "<br><br>" +
                                "Kunjungi beberapa URL di bawah ini untuk portofolio/proyek saya lainnya:" +
                                "<ul>" +
                                "<li>[Portofolio Website](https://hq.achmodez.tech)</li>" +
                                "<li>[Linkedin Profile](https://www.linkedin.com/in/habbatul/)</li>" +
                                "<ul>")
                        .version("1.0.0")
                );
    }
}
