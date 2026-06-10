<!DOCTYPE html>
<html lang="en" class="dark">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>LuxeBite - Gourmet Delivered</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet" />
            <style>
                .scrollbar-hide {
                    -ms-overflow-style: none;
                    scrollbar-width: none;
                }

                .scrollbar-hide::-webkit-scrollbar {
                    display: none;
                }
            </style>
        </head>

        <body class="bg-gray-950 text-white min-h-screen">

            <!-- Navbar -->
            <nav class="bg-black/90 backdrop-blur-lg border-b border-amber-900/40 sticky top-0 z-50">
                <div class="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
                    <div class="flex items-center space-x-4">
                        <h1 class="text-4xl font-bold text-amber-400 tracking-wider">LuxeBite</h1>
                    </div>
                    <div class="flex items-center space-x-6">
                        <div
                            class="hidden md:flex items-center space-x-3 bg-gray-800/60 px-4 py-2 rounded-full border border-amber-800/50">
                            <i class="fas fa-map-marker-alt text-amber-400"></i>
                            <span class="text-sm">Delivering to <strong>Downtown Manhattan</strong></span>
                        </div>
                        <a href="${pageContext.request.contextPath}/cart"
                            class="relative hover:text-amber-400 transition" title="Cart">
                            <i class="fas fa-shopping-bag text-2xl"></i>
                            <span id="cartBadge"
                                class="absolute -top-2 -right-3 bg-amber-500 text-black text-xs font-bold rounded-full h-6 w-6 flex items-center justify-center"
                                style="display: ${pendingItemCount > 0 ? 'flex' : 'none'}">${pendingItemCount}</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/profile"
                            class="flex items-center space-x-2 hover:text-amber-400 transition" title="Profile">
                            <i class="fas fa-user-circle text-2xl"></i>
                            <span class="hidden md:inline">${LOGGED_IN_USER.username}</span>
                        </a>
                        <button onclick="logout()" class="flex items-center space-x-2 hover:text-red-400 transition"
                            title="Logout">
                            <i class="fas fa-sign-out-alt text-2xl"></i>
                            <span class="hidden md:inline">Logout</span>
                        </button>
                    </div>
                </div>
            </nav>

            <!-- Category Navigation Bar -->
            <div class="bg-gray-900/95 backdrop-blur-lg border-b border-amber-900/30 sticky top-[72px] z-40">
                <div class="max-w-7xl mx-auto px-6">
                    <div
                        class="flex items-center justify-center space-x-2 md:space-x-6 overflow-x-auto scrollbar-hide py-4">
                        <a href="#top-picks"
                            class="flex items-center space-x-2 px-4 py-2 rounded-lg bg-amber-600/20 hover:bg-amber-600/40 text-amber-400 transition whitespace-nowrap">
                            <i class="fas fa-crown"></i>
                            <span class="font-medium">Top Picks</span>
                        </a>
                        <a href="#main-dish"
                            class="flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-amber-600/20 text-gray-300 hover:text-amber-400 transition whitespace-nowrap">
                            <i class="fas fa-drumstick-bite"></i>
                            <span class="font-medium">Main Dish</span>
                        </a>
                        <a href="#side-dish"
                            class="flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-amber-600/20 text-gray-300 hover:text-amber-400 transition whitespace-nowrap">
                            <i class="fas fa-pepper-hot"></i>
                            <span class="font-medium">Appetizers</span>
                        </a>
                        <a href="#dessert"
                            class="flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-amber-600/20 text-gray-300 hover:text-amber-400 transition whitespace-nowrap">
                            <i class="fas fa-ice-cream"></i>
                            <span class="font-medium">Dessert</span>
                        </a>
                        <a href="#drinks"
                            class="flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-amber-600/20 text-gray-300 hover:text-amber-400 transition whitespace-nowrap">
                            <i class="fas fa-wine-glass-alt"></i>
                            <span class="font-medium">Drinks</span>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Main Content -->
            <main id="mainContent" class="w-full">

                <!-- Hero Welcome -->
                <section class="bg-gradient-to-br from-amber-900/30 via-black to-purple-900/30 py-20 px-10">
                    <div class="max-w-6xl mx-auto text-center">
                        <h1 class="text-6xl md:text-8xl font-extrabold text-amber-400 mb-4">Good Evening,
                            ${LOGGED_IN_USER.username}</h1>
                        <p class="text-2xl text-gray-300">Indulge in gourmet masterpieces tonight</p>
                    </div>
                </section>

                <!-- Chef's Top Picks -->
                <section id="top-picks" class="py-16 px-6 lg:px-10 bg-gray-950">
                    <div class="max-w-6xl mx-auto">
                        <h2
                            class="text-4xl font-bold text-amber-400 mb-12 flex items-center justify-center md:justify-start">
                            <i class="fas fa-crown mr-4"></i> Chef's Top Picks
                        </h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                            <c:forEach var="product" items="${topSellingProducts}">
                                <div class="bg-gray-900 rounded-2xl overflow-hidden shadow-2xl border border-amber-900/20 
                                        hover:shadow-amber-900/50 transition-all duration-300 group relative">

                                    <!-- Clickable Image (opens modal with details) -->
                                    <div class="cursor-pointer overflow-hidden" data-id="${product.id()}"
                                        data-name="${fn:escapeXml(product.name())}" data-price="${product.price()}"
                                        data-description="${fn:escapeXml(product.description())}"
                                        data-stock="${product.stock()}"
                                        data-image="${pageContext.request.contextPath}${product.imageUrl()}"
                                        onclick="openProductModal(this)">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl()}"
                                            alt="${product.name()}"
                                            class="w-full h-64 object-cover transition-transform duration-500 group-hover:scale-110">
                                    </div>

                                    <!-- Minimal Info + Add to Cart -->
                                    <div class="p-5 relative flex items-center justify-between">
                                        <div>
                                            <h3 class="text-xl font-bold text-amber-400">${product.name()}</h3>
                                            <span class="text-2xl font-bold text-white">$${product.price()}</span>
                                        </div>
                                        <button type="button" onclick="addToCart(${product.id()}, ${product.price()})"
                                            class="w-12 h-12 bg-amber-600 hover:bg-amber-500 text-black 
                                               rounded-full shadow-xl flex items-center justify-center 
                                               transition-all duration-300 hover:scale-110 hover:shadow-amber-500/60
                                               border-4 border-gray-900" title="Add to cart">
                                            <i class="fas fa-shopping-bag text-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </section>

                <section id="main-dish" class="py-16 px-6 lg:px-10 bg-black/50">
                    <div class="max-w-6xl mx-auto">
                        <h2 class="text-4xl font-bold text-amber-400 mb-10">Main Dish</h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                            <c:forEach var="product" items="${mainProducts}">
                                <div class="bg-gray-900 rounded-2xl overflow-hidden shadow-2xl border border-amber-900/20 
                                        hover:shadow-amber-900/50 transition-all duration-300 group relative">

                                    <div class="cursor-pointer overflow-hidden" data-id="${product.id()}"
                                        data-name="${fn:escapeXml(product.name())}" data-price="${product.price()}"
                                        data-description="${fn:escapeXml(product.description())}"
                                        data-stock="${product.stock()}"
                                        data-image="${pageContext.request.contextPath}${product.imageUrl()}"
                                        onclick="openProductModal(this)">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl()}"
                                            alt="${product.name()}"
                                            class="w-full h-56 object-cover transition-transform duration-500 group-hover:scale-110">
                                    </div>

                                    <div class="p-5 relative flex items-center justify-between">
                                        <div>
                                            <h3 class="text-xl font-bold text-amber-400">
                                                ${product.name()}</h3>
                                            <span class="text-2xl font-bold text-white">$${product.price()}</span>
                                        </div>
                                        <button type="button" onclick="addToCart(${product.id()}, ${product.price()})"
                                            class="w-12 h-12 bg-amber-600 hover:bg-amber-500 text-black 
                                               rounded-full shadow-xl flex items-center justify-center 
                                               transition-all duration-300 hover:scale-110 hover:shadow-amber-500/60
                                               border-4 border-gray-900" title="Add to cart">
                                            <i class="fas fa-shopping-bag text-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </section>

                <section id="side-dish" class="py-16 px-6 lg:px-10 bg-gray-950">
                    <div class="max-w-6xl mx-auto">
                        <h2 class="text-4xl font-bold text-amber-400 mb-10">Appetizers</h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                            <c:forEach var="product" items="${appetizerProducts}">
                                <div class="bg-gray-900 rounded-2xl overflow-hidden shadow-2xl border border-amber-900/20 
                                        hover:shadow-amber-900/50 transition-all duration-300 group relative">

                                    <div class="cursor-pointer overflow-hidden" data-id="${product.id()}"
                                        data-name="${fn:escapeXml(product.name())}" data-price="${product.price()}"
                                        data-description="${fn:escapeXml(product.description())}"
                                        data-stock="${product.stock()}"
                                        data-image="${pageContext.request.contextPath}${product.imageUrl()}"
                                        onclick="openProductModal(this)">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl()}"
                                            alt="${product.name()}"
                                            class="w-full h-56 object-cover transition-transform duration-500 group-hover:scale-110">
                                    </div>

                                    <div class="p-5 relative flex items-center justify-between">
                                        <div>
                                            <h3 class="text-xl font-bold text-amber-400">
                                                ${product.name()}</h3>
                                            <span class="text-2xl font-bold text-white">$${product.price()}</span>
                                        </div>
                                        <button type="button" onclick="addToCart(${product.id()}, ${product.price()})"
                                            class="w-12 h-12 bg-amber-600 hover:bg-amber-500 text-black 
                                               rounded-full shadow-xl flex items-center justify-center 
                                               transition-all duration-300 hover:scale-110 hover:shadow-amber-500/60
                                               border-4 border-gray-900" title="Add to cart">
                                            <i class="fas fa-shopping-bag text-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </section>

                <section id="dessert" class="py-16 px-6 lg:px-10 bg-black/50">
                    <div class="max-w-6xl mx-auto">
                        <h2 class="text-4xl font-bold text-amber-400 mb-10">Dessert</h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                            <c:forEach var="product" items="${dessertProducts}">
                                <div class="bg-gray-900 rounded-2xl overflow-hidden shadow-2xl border border-amber-900/20 
                                        hover:shadow-amber-900/50 transition-all duration-300 group relative">

                                    <div class="cursor-pointer overflow-hidden" data-id="${product.id()}"
                                        data-name="${fn:escapeXml(product.name())}" data-price="${product.price()}"
                                        data-description="${fn:escapeXml(product.description())}"
                                        data-stock="${product.stock()}"
                                        data-image="${pageContext.request.contextPath}${product.imageUrl()}"
                                        onclick="openProductModal(this)">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl()}"
                                            alt="${product.name()}"
                                            class="w-full h-56 object-cover transition-transform duration-500 group-hover:scale-110">
                                    </div>

                                    <div class="p-5 relative flex items-center justify-between">
                                        <div>
                                            <h3 class="text-xl font-bold text-amber-400">
                                                ${product.name()}</h3>
                                            <span class="text-2xl font-bold text-white">$${product.price()}</span>
                                        </div>
                                        <button type="button" onclick="addToCart(${product.id()}, ${product.price()})"
                                            class="w-12 h-12 bg-amber-600 hover:bg-amber-500 text-black 
                                               rounded-full shadow-xl flex items-center justify-center 
                                               transition-all duration-300 hover:scale-110 hover:shadow-amber-500/60
                                               border-4 border-gray-900" title="Add to cart">
                                            <i class="fas fa-shopping-bag text-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </section>

                <section id="drinks" class="py-16 px-6 lg:px-10 bg-gray-950">
                    <div class="max-w-6xl mx-auto">
                        <h2 class="text-4xl font-bold text-amber-400 mb-10">Drinks</h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                            <c:forEach var="product" items="${drinkProducts}">
                                <div class="bg-gray-900 rounded-2xl overflow-hidden shadow-2xl border border-amber-900/20 
                                        hover:shadow-amber-900/50 transition-all duration-300 group relative">

                                    <div class="cursor-pointer overflow-hidden" data-id="${product.id()}"
                                        data-name="${fn:escapeXml(product.name())}" data-price="${product.price()}"
                                        data-description="${fn:escapeXml(product.description())}"
                                        data-stock="${product.stock()}"
                                        data-image="${pageContext.request.contextPath}${product.imageUrl()}"
                                        onclick="openProductModal(this)">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl()}"
                                            alt="${product.name()}"
                                            class="w-full h-56 object-cover transition-transform duration-500 group-hover:scale-110">
                                    </div>

                                    <div class="p-5 relative flex items-center justify-between">
                                        <div>
                                            <h3 class="text-xl font-bold text-amber-400">
                                                ${product.name()}</h3>
                                            <span class="text-2xl font-bold text-white">$${product.price()}</span>
                                        </div>
                                        <button type="button" onclick="addToCart(${product.id()}, ${product.price()})"
                                            class="w-12 h-12 bg-amber-600 hover:bg-amber-500 text-black 
                                               rounded-full shadow-xl flex items-center justify-center 
                                               transition-all duration-300 hover:scale-110 hover:shadow-amber-500/60
                                               border-4 border-gray-900" title="Add to cart">
                                            <i class="fas fa-shopping-bag text-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </section>

            </main>
            </div>

            <!-- Product Detail Modal -->
            <div id="productModal" class="fixed inset-0 z-50 hidden p-4">
                <div class="absolute inset-0 bg-black/70" onclick="closeProductModal()"></div>
                <div
                    class="relative max-w-4xl w-full bg-gray-950 border border-amber-900/40 rounded-3xl shadow-2xl overflow-hidden">
                    <div class="flex justify-end p-4">
                        <button onclick="closeProductModal()" class="text-amber-300 hover:text-amber-100 text-2xl">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 p-8 max-h-[90vh] overflow-y-auto" id="modalBody">
                        <div>
                            <img id="modalImage" src="" alt="" class="w-full rounded-2xl shadow-2xl object-cover h-80">
                        </div>
                        <div class="space-y-6">
                            <div>
                                <h1 id="modalName" class="text-4xl font-extrabold text-amber-400"></h1>
                                <p id="modalDescription" class="text-gray-300 mt-3"></p>
                            </div>
                            <div class="text-4xl font-bold" id="modalPrice"></div>
                            <div id="modalStock" class="text-green-400 font-bold text-lg flex items-center">
                                <i class="fas fa-check-circle mr-2"></i>
                            </div>
                            <div class="flex gap-4">
                                <div class="flex-1">
                                    <label class="block text-sm text-gray-400 mb-2">Quantity</label>
                                    <input type="number" id="modalQuantity" value="1" min="1" max="100"
                                        class="w-full bg-gray-800 text-white px-4 py-2 rounded-lg border border-amber-600/50 focus:border-amber-400 outline-none">
                                </div>
                            </div>
                            <button type="button" id="modalAddToCart" onclick="addToCartFromModal()"
                                class="block w-full text-center bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-black font-bold text-xl py-4 rounded-xl transition transform hover:scale-105">
                                Add to Order
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Toast Container -->
            <div id="toastContainer" class="fixed top-20 right-6 z-[60] space-y-3"></div>

            <script>
                const ctx = "${pageContext.request.contextPath}";
                const csrfToken = "${sessionScope.CSRF_TOKEN}";
                let currentProductData = {};

                // Toast notification function
                function showToast(message, type = 'success') {
                    const container = document.getElementById('toastContainer');
                    const toast = document.createElement('div');

                    const bgColor = type === 'success' ? 'bg-green-600' : 'bg-red-600';
                    const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';

                    toast.className = bgColor + ' text-white px-6 py-4 rounded-lg shadow-2xl flex items-center space-x-3 min-w-[300px] transform transition-all duration-300 translate-x-[400px] opacity-0';
                    toast.innerHTML = '<i class="fas ' + icon + ' text-2xl"></i>' +
                        '<span class="flex-1 font-medium">' + message + '</span>' +
                        '<button onclick="this.parentElement.remove()" class="text-white hover:text-gray-200">' +
                        '<i class="fas fa-times"></i>' +
                        '</button>';

                    container.appendChild(toast);

                    // Trigger animation
                    setTimeout(() => {
                        toast.classList.remove('translate-x-[400px]', 'opacity-0');
                    }, 10);

                    // Auto remove after 4 seconds
                    setTimeout(() => {
                        toast.classList.add('translate-x-[400px]', 'opacity-0');
                        setTimeout(() => toast.remove(), 300);
                    }, 4000);
                }

                // Check for messages from session
                document.addEventListener('DOMContentLoaded', function () {
                    const successMessage = "${sessionScope.successMessage}";
                    const errorMessage = "${sessionScope.errorMessage}";

                    if (successMessage && successMessage !== '') {
                        showToast(successMessage, 'success');
                    }

                    if (errorMessage && errorMessage !== '') {
                        showToast(errorMessage, 'error');
                    }
                });

                function openProductModal(el) {
                    const modal = document.getElementById('productModal');
                    if (!modal) return;

                    currentProductData = {
                        id: el.dataset.id,
                        name: el.dataset.name,
                        price: el.dataset.price,
                        description: el.dataset.description,
                        stock: el.dataset.stock,
                        image: el.dataset.image
                    };

                    document.getElementById('modalImage').src = currentProductData.image;
                    document.getElementById('modalImage').alt = currentProductData.name;
                    document.getElementById('modalName').innerText = currentProductData.name;
                    document.getElementById('modalDescription').innerText = currentProductData.description || '';
                    document.getElementById('modalPrice').innerText = '$' + parseFloat(currentProductData.price).toFixed(2);
                    document.getElementById('modalStock').innerHTML = '<i class="fas fa-check-circle mr-2"></i> In stock: ' + currentProductData.stock;
                    document.getElementById('modalQuantity').value = 1;

                    modal.style.display = 'flex';
                    modal.style.alignItems = 'center';
                    modal.style.justifyContent = 'center';
                    document.body.style.overflow = 'hidden';
                }

                function closeProductModal() {
                    const modal = document.getElementById('productModal');
                    if (!modal) return;

                    modal.style.display = 'none';
                    document.body.style.overflow = 'auto';
                    currentProductData = {};
                }

                function addToCart(productId, productPrice, quantity = 1) {
                    const params = new URLSearchParams();
                    params.append('productId', productId);
                    params.append('productPrice', productPrice);
                    params.append('quantity', quantity);

                    fetch(ctx + '/cart/add', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'X-CSRF-TOKEN': csrfToken
                        },
                        body: params
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                showToast(data.message || 'Added to cart successfully!', 'success');
                                updateCartBadge(data.pendingItemCount);
                            } else {
                                showToast(data.message || 'Failed to add to cart', 'error');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            showToast('An error occurred. Please try again.', 'error');
                        });
                }

                function updateCartBadge(count) {
                    const badge = document.getElementById('cartBadge');
                    if (badge) {
                        if (count > 0) {
                            badge.textContent = count;
                            badge.style.display = 'flex';
                        } else {
                            badge.style.display = 'none';
                        }
                    }
                }

                function addToCartFromModal() {
                    const quantity = parseInt(document.getElementById('modalQuantity').value) || 1;
                    addToCart(currentProductData.id, currentProductData.price, quantity);
                    closeProductModal();
                }

                // Smooth scroll for category links
                document.addEventListener('DOMContentLoaded', function () {
                    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                        anchor.addEventListener('click', function (e) {
                            e.preventDefault();
                            const target = document.querySelector(this.getAttribute('href'));
                            if (target) {
                                const offset = 160; // Account for sticky navbars
                                const targetPosition = target.offsetTop - offset;
                                window.scrollTo({
                                    top: targetPosition,
                                    behavior: 'smooth'
                                });
                            }
                        });
                    });
                });

                // Logout function
                function logout() {
                    if (confirm('Are you sure you want to logout?')) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = '${pageContext.request.contextPath}/logout';

                        const csrfInput = document.createElement('input');
                        csrfInput.type = 'hidden';
                        csrfInput.name = 'CSRF_TOKEN';
                        csrfInput.value = '${CSRF_TOKEN}';

                        form.appendChild(csrfInput);
                        document.body.appendChild(form);
                        form.submit();
                    }
                }
            </script>
        </body>

</html>