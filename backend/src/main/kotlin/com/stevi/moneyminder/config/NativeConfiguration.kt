package com.stevi.moneyminder.config

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.stevi.moneyminder.model.response.MonoBankExchangeRateResponse
import com.stevi.moneyminder.model.response.MonoBankTransactionResponse
import com.stevi.moneyminder.repository.projection.AccountMonoBankTokenProjection
import com.stevi.moneyminder.repository.projection.TopExpensesProjection
import io.jsonwebtoken.impl.DefaultClaimsBuilder
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.jsonwebtoken.impl.DefaultJwtHeaderBuilder
import io.jsonwebtoken.impl.DefaultJwtParserBuilder
import io.jsonwebtoken.impl.io.StandardCompressionAlgorithms
import io.jsonwebtoken.impl.security.DefaultDynamicJwkBuilder
import io.jsonwebtoken.impl.security.DefaultJwkParserBuilder
import io.jsonwebtoken.impl.security.DefaultJwkSetBuilder
import io.jsonwebtoken.impl.security.DefaultJwkSetParserBuilder
import io.jsonwebtoken.impl.security.DefaultKeyOperationBuilder
import io.jsonwebtoken.impl.security.DefaultKeyOperationPolicyBuilder
import io.jsonwebtoken.impl.security.JwksBridge
import io.jsonwebtoken.impl.security.KeysBridge
import io.jsonwebtoken.impl.security.StandardEncryptionAlgorithms
import io.jsonwebtoken.impl.security.StandardKeyAlgorithms
import io.jsonwebtoken.impl.security.StandardKeyOperations
import io.jsonwebtoken.impl.security.StandardSecureDigestAlgorithms
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.collections.toTypedArray
import kotlin.jvm.java
import liquibase.changelog.ChangeLogHistoryServiceFactory
import liquibase.changelog.FastCheckService
import liquibase.changelog.StandardChangeLogHistoryService
import liquibase.changelog.visitor.ValidatingVisitorGeneratorFactory
import liquibase.database.LiquibaseTableNamesFactory
import liquibase.parser.SqlParserFactory
import liquibase.report.ShowSummaryGeneratorFactory
import liquibase.ui.LoggerUIService
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import java.util.UUID
import kotlin.collections.emptyList

@RegisterReflectionForBinding(
    classes = [
        KeysBridge::class,
        StandardEncryptionAlgorithms::class,
        StandardKeyAlgorithms::class,
        StandardSecureDigestAlgorithms::class,
        StandardCompressionAlgorithms::class,
        StandardKeyOperations::class,
        JwksBridge::class,
        DefaultJwtBuilder.Supplier::class,
        DefaultJwtParserBuilder.Supplier::class,
        DefaultJwtHeaderBuilder.Supplier::class,
        DefaultClaimsBuilder.Supplier::class,
        DefaultDynamicJwkBuilder.Supplier::class,
        DefaultJwkParserBuilder.Supplier::class,
        DefaultJwkSetBuilder.Supplier::class,
        DefaultJwkSetParserBuilder.Supplier::class,
        DefaultKeyOperationBuilder.Supplier::class,
        DefaultKeyOperationPolicyBuilder.Supplier::class,
        GoogleAuthorizationCodeRequestUrl::class,
        AuthorizationCodeRequestUrl::class,
        AuthorizationCodeResponseUrl::class,
        GoogleAuthorizationCodeTokenRequest::class,
        GoogleTokenResponse::class,
        GoogleIdToken::class,
        GoogleIdToken.Payload::class,
        NetHttpTransport::class,
        GsonFactory::class,
        HttpServletRequest::class,
        HttpServletResponse::class,
        LoggerUIService::class,
        ChangeLogHistoryServiceFactory::class,
        FastCheckService::class,
        LiquibaseTableNamesFactory::class,
        StandardChangeLogHistoryService::class,
        ValidatingVisitorGeneratorFactory::class,
        SqlParserFactory::class,
        ShowSummaryGeneratorFactory::class,
        MonoBankTransactionResponse::class,
        MonoBankExchangeRateResponse::class
    ]
)
@Configuration
@ImportRuntimeHints(CustomRuntimeHints::class)
class NativeConfiguration

class CustomRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        addEhcacheHints(hints)
        addLiquiBaseHints(hints)
        addProjectionsHits(hints)
        addCustomJsonResourcesHints(hints)
        addKotlinInternalHits(hints)
        addUuidArrayHints(hints)
        addJaxbHints(hints)
        addJavaCollectionsHints(hints)
    }

    private fun addEhcacheHints(hints: RuntimeHints) {
        hints.resources().registerPattern("ehcache.xml")
    }

    private fun addLiquiBaseHints(hints: RuntimeHints) {
        hints.resources().registerPattern("db/scripts/*.sql")
        hints.resources().registerPattern("db/changelog-master.yaml")
    }

    private fun addCustomJsonResourcesHints(hints: RuntimeHints) {
        hints.resources().registerPattern("default/*.json");
    }

    private fun addProjectionsHits(hints: RuntimeHints) {
        hints.reflection().registerType(
            AccountMonoBankTokenProjection::class.java,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.DECLARED_FIELDS,
            MemberCategory.PUBLIC_FIELDS,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
            MemberCategory.INTROSPECT_DECLARED_METHODS,
            MemberCategory.INTROSPECT_PUBLIC_METHODS
        )

        hints.reflection().registerType(
            TopExpensesProjection::class.java,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.DECLARED_FIELDS,
            MemberCategory.PUBLIC_FIELDS,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
            MemberCategory.INTROSPECT_DECLARED_METHODS,
            MemberCategory.INTROSPECT_PUBLIC_METHODS
        )
    }

    private fun addKotlinInternalHits(hints: RuntimeHints) {
        hints.reflection().registerType(
            Class.forName("kotlin.collections.EmptyList"),
            *MemberCategory.entries.toTypedArray()
        )
        hints.reflection().registerType(
            Class.forName("kotlin.collections.EmptyMap"),
            *MemberCategory.entries.toTypedArray()
        )
        hints.reflection().registerType(
            Class.forName("kotlin.collections.EmptySet"),
            *MemberCategory.entries.toTypedArray()
        )
        hints.reflection().registerType(
            java.util.ArrayList::class.java,
            *MemberCategory.entries.toTypedArray()
        )
    }

    private fun addUuidArrayHints(hints: RuntimeHints) {
        hints.reflection().registerType(
            Array<UUID>::class.java,
            *MemberCategory.entries.toTypedArray()
        )
    }

    private fun addJavaCollectionsHints(hints: RuntimeHints) {
        hints.reflection().registerType(
            java.util.ArrayList::class.java,
            *MemberCategory.entries.toTypedArray()
        )
        
        hints.reflection().registerType(
            java.util.LinkedList::class.java,
            *MemberCategory.entries.toTypedArray()
        )
        
        hints.reflection().registerType(
            java.util.HashMap::class.java,
            *MemberCategory.entries.toTypedArray()
        )
        
        hints.reflection().registerType(
            java.util.LinkedHashMap::class.java,
            *MemberCategory.entries.toTypedArray()
        )
        
        hints.reflection().registerType(
            java.util.HashSet::class.java,
            *MemberCategory.entries.toTypedArray()
        )
        
        hints.reflection().registerType(
            java.util.LinkedHashSet::class.java,
            *MemberCategory.entries.toTypedArray()
        )
    }

    private fun addJaxbHints(hints: RuntimeHints) {
        val jaxbClasses = listOf(
            "org.glassfish.jaxb.runtime.v2.runtime.property.ArrayElementNodeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
            "org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementPropertyInfo",
            "org.glassfish.jaxb.runtime.v2.runtime.property.PropertyFactory",
            "org.glassfish.jaxb.runtime.v2.runtime.property.AttributeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.SingleElementNodeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.SingleReferenceNodeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.ArrayReferenceNodeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.ValueProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor",
            "org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallerImpl",
            "org.glassfish.jaxb.runtime.v2.runtime.property.ArrayElementLeafProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.SingleElementLeafProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.SingleMapNodeProperty",
            "org.glassfish.jaxb.runtime.v2.runtime.property.RuntimeModelBuilder",
            "org.glassfish.jaxb.runtime.v2.model.impl.Utils",
            "org.glassfish.jaxb.runtime.v2.JAXBContextFactory",
            "org.glassfish.jaxb.runtime.v2.ContextFactory",
            "org.glassfish.jaxb.core.v2.model.nav.ReflectionNavigator",
            "jakarta.xml.bind.annotation.W3CDomHandler",
            "jakarta.xml.bind.Binder",
            "jakarta.xml.bind.annotation.XmlAnyElement",
            "jakarta.xml.bind.annotation.XmlElement",
            "jakarta.xml.bind.annotation.XmlElementRef",
            "jakarta.xml.bind.annotation.XmlElementDecl",
            "jakarta.xml.bind.annotation.XmlEnum",
            "jakarta.xml.bind.annotation.XmlSeeAlso",
            "jakarta.xml.bind.annotation.XmlType",
            "jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter",
            "jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter",
            "jakarta.xml.ws.WebServiceRef"
        )

        for (className in jaxbClasses) {
            try {
                val clazz = Class.forName(className)
                hints.reflection().registerType(
                    clazz,
                    *MemberCategory.entries.toTypedArray()
                )
            } catch (e: ClassNotFoundException) {
                // Class not available on classpath, skip it
            }
        }
    }
}
