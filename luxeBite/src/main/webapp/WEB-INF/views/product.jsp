<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <c:set var="img"
            value="${empty product.imageUrl ? 'https://images.unsplash.com/photo-1604909052743-94e838986d24?w=1200&q=80' : product.imageUrl}" />
        <c:set var="name" value="${empty product.name ? 'Unknown Dish' : product.name}" />
        <c:set var="price" value="${product.price}" />
        <c:set var="stock" value="${product.stock}" />
        <c:set var="desc" value="${empty product.description ? 'No description available.' : product.description}" />

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-10">
            <!-- Image -->
            <div>
                <img src="${img}" alt="${fn:escapeXml(name)}" class="w-full rounded-2xl shadow-2xl object-cover">
            </div>

            <!-- Details -->
            <div class="space-y-8">
                <div>
                    <h1 class="text-4xl md:text-5xl font-extrabold text-amber-400">${fn:escapeXml(name)}</h1>
                    <p class="text-lg text-gray-400 mt-3">
                        <c:out value="${desc}" />
                    </p>
                </div>

                <div class="text-5xl md:text-6xl font-bold">
                    $
                    <c:out value="${price}" />
                </div>

                <div class="text-green-400 font-bold text-lg flex items-center">
                    <i class="fas fa-check-circle mr-2"></i>
                    <c:choose>
                        <c:when test="${stock gt 0}">In Stock – ${stock} left</c:when>
                        <c:otherwise>Currently unavailable</c:otherwise>
                    </c:choose>
                </div>

                <div>
                    <h3 class="text-2xl font-bold text-amber-300 mb-4">Description</h3>
                    <p class="text-gray-300 leading-relaxed">
                        <c:out value="${desc}" />
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/cart/add?productId=${product.id}&quantity=1"
                    class="block text-center bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-black font-bold text-xl py-6 rounded-xl transition transform hover:scale-105">
                    Add to Order – $
                    <c:out value="${price}" />
                </a>
            </div>
        </div>