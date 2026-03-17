package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.*;
import com.reyn.objects.dto.CreateProductDTO;
import com.reyn.objects.vo.UpdateProductPreviewVO;
import com.reyn.objects.dto.ProductImageDTO;
import com.reyn.objects.dto.SkuDTO;
import com.reyn.objects.entity.*;
import com.reyn.objects.vo.*;
import com.reyn.service.ProductImageService;
import com.reyn.service.ProductService;
import com.reyn.service.SkuService;
import com.reyn.service.UploadService;
import com.reyn.utils.LoginHelper;
import com.reyn.utils.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private UploadService uploadService;

    @Override
    @Transactional
    public SaResult createProduct(CreateProductDTO productDTO, Long userId) {
        // 1. 校验商家身份
        QueryWrapper<Merchant> merchantQueryWrapper = new QueryWrapper<>();
        merchantQueryWrapper.eq("user_id", userId);
        Merchant merchant = merchantMapper.selectOne(merchantQueryWrapper);
        if (merchant == null) {
            return SaResult.error("当前用户非商家，无法添加商品");
        }

        // 2. 保存商品基本信息
        Product product = new Product();
        product.setCategoryId(productDTO.getCategoryId());
        product.setTitle(productDTO.getTitle());
        product.setBrief(productDTO.getBrief());
        product.setDescription(productDTO.getDescription());
        product.setProductStatus(productDTO.getProductStatus());
        product.setMerchantId(merchant.getId());
        product.setSallerId(userId);
        this.save(product); // 插入商品并生成 product_id

        // 3. 保存 SKU 信息
        for (CreateProductDTO.CreateSkuDTO skuDTO : productDTO.getSkus()) {
            Sku sku = new Sku();
            sku.setProductId(product.getId());
            sku.setSpecification(skuDTO.getSpecification());
            sku.setSkuStatus(skuDTO.getSkuStatus());
            sku.setPrice(skuDTO.getPrice());
            sku.setStock(skuDTO.getStock());
            skuMapper.insert(sku);
        }

        // 4. 保存图片信息
        boolean isFirstImage = true;
        for (String imgUrl : productDTO.getImageUrls()) {
            ProductImage image = new ProductImage();
            image.setProductId(product.getId());
            image.setUrl(imgUrl);
            image.setImageType((byte) (isFirstImage ? 0 : 1)); // 第一张为主图，其余为副图
            productImageMapper.insert(image);
            isFirstImage = false;
        }

        // 5. 返回成功结果
        return SaResult.ok("商品创建成功").setData(product.getId());
    }

    @Override
    public UpdateProductPreviewVO getProductRelativeInfo(long productId) {
        // 1. 查询商品基本信息
        Product product = this.getById(productId);
        if (product == null) {
            return null; // 商品不存在时返回 null
        }

        // 2. 构造返回对象
        UpdateProductPreviewVO productVO = new UpdateProductPreviewVO();
        productVO.setProductId(product.getId());
        productVO.setCategoryId(product.getCategoryId());
        productVO.setTitle(product.getTitle());
        productVO.setDescription(product.getDescription());
        productVO.setBrief(product.getBrief());
        productVO.setProductStatus(product.getProductStatus());

        // 3. 查询 SKU 信息
        LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(Sku::getProductId, productId);
        List<Sku> skuList = skuMapper.selectList(skuWrapper);

        // 将 Sku 转换为 UpdateSkuDTO
        List<UpdateProductPreviewVO.UpdateSkuDTO> skuDTOList = skuList.stream().map(sku -> {
            UpdateProductPreviewVO.UpdateSkuDTO skuDTO = new UpdateProductPreviewVO.UpdateSkuDTO();
            skuDTO.setSkuId(sku.getId());
            skuDTO.setSpecification(sku.getSpecification());
            skuDTO.setSkuStatus(sku.getSkuStatus());
            skuDTO.setPrice(sku.getPrice());
            skuDTO.setStock(sku.getStock());
            return skuDTO;
        }).collect(Collectors.toList());

        productVO.setSkus(skuDTOList);

        // 4. 查询图片信息
        LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(ProductImage::getProductId, productId);
        List<ProductImage> images = productImageMapper.selectList(imageWrapper);

        // 提取图片 URL 列表
        List<String> imageUrls = images.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
        productVO.setImageUrls(imageUrls);

        // 5. 查询分类名称（可选）
//        if (product.getCategoryId() != null) {
//            ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
//            if (category != null) {
//                // 这里可以根据需要扩展 VO 来包含分类名称
//            }
//        }

        return productVO;
    }


    @Override
    @Transactional
    public SaResult updateProduct(CreateProductDTO productDTO) {
        // 更新商品基本信息
        Product product = new Product();
        product.setId(productDTO.getProductId());
        BeanUtils.copyProperties(productDTO, product);

        this.updateById(product);

        // 更新SKU信息
        if (productDTO.getSkus() != null && !productDTO.getSkus().isEmpty()) {
            // 添加新的SKU
            for (CreateProductDTO.CreateSkuDTO skuDTO : productDTO.getSkus()) {
                Sku sku = new Sku();
                sku.setId(skuDTO.getSkuId());
                BeanUtils.copyProperties(skuDTO, sku);
                sku.setProductId(product.getId());
                skuMapper.updateById(sku);
            }
        }

        // 更新图片信息
        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            // 删除原有图片
            LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
            imageWrapper.eq(ProductImage::getProductId, product.getId());
            productImageMapper.delete(imageWrapper);

            // 添加新图片
            boolean is_first = true;
            for (String url : productDTO.getImageUrls()) {
                ProductImage image = new ProductImage();
                image.setProductId(product.getId());
                image.setUrl(url);
                if (is_first){
                    image.setImageType((byte) 0);
                    is_first = false;
                }else {
                    image.setImageType((byte) 1);
                }
                productImageMapper.insert(image);
            }
        }

        return SaResult.ok("商品更新成功");
    }


    @Override
    @Transactional
    public SaResult deleteProduct(Long productId) {
        // 删除商品
        this.removeById(productId);

        // 删除相关SKU
        LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(Sku::getProductId, productId);
        skuMapper.delete(skuWrapper);

        // 删除相关图片
        LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(ProductImage::getProductId, productId);
        productImageMapper.delete(imageWrapper);

        return SaResult.ok("商品删除成功");
    }

    @Override
    public SaResult getProductDetail(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            return SaResult.error("商品不存在");
        }

        // 获取商品详情VO
        ProductInfoVO productVO = new ProductInfoVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setId(product.getId());
        productVO.setTitle(product.getTitle());
        productVO.setDescription(product.getDescription());
        productVO.setMerchantId(product.getMerchantId());
        productVO.setSallerId(product.getSallerId());

        // 检查是否收藏
        int checkResult = userFavoriteMapper.checkUserFavorite(LoginHelper.getLoginUserId(), productId);
        if (checkResult > 0) productVO.setHasFavorited(true);

        // 获取分类名称
        if (product.getCategoryId() != null) {
            ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                productVO.setCategoryName(category.getName());
            }
        }

        // 获取SKU信息
        LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(Sku::getProductId, productId);
        List<Sku> skuList = skuMapper.selectList(skuWrapper);

        List<SkuVO> skuVOList = skuList.stream().map(sku -> {
            SkuVO skuVO = new SkuVO();
            skuVO.setId(sku.getId());
            skuVO.setSpecification(sku.getSpecification());
            skuVO.setSkuStatus(sku.getSkuStatus());
            skuVO.setPrice(sku.getPrice());
            skuVO.setStock(sku.getStock());
            return skuVO;
        }).collect(Collectors.toList());

        productVO.setSkus(skuVOList);

        // 获取图片信息
        LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(ProductImage::getProductId, productId);
        List<ProductImage> images = productImageMapper.selectList(imageWrapper);

        List<String> imageUrls = images.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
        productVO.setImageUrls(imageUrls);

        // 设置主图
        ProductImage mainImage = images.stream()
                .filter(img -> img.getImageType() != null && img.getImageType() == 0) // 0是主图
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
        if (mainImage != null) {
            productVO.setMainImageUrl(mainImage.getUrl());
        }

        // 计算价格
        if (!skuList.isEmpty()) {
            List<BigDecimal> prices = skuList.stream()
                    .map(Sku::getPrice)
                    .sorted()
                    .collect(Collectors.toList());
            productVO.setMinPrice(prices.get(0));
            productVO.setMaxPrice(prices.get(prices.size() - 1));
        }

        return SaResult.ok("获取商品详情成功").setData(productVO);
    }

    // 添加获取商品总览列表的方法
    @Override
    public SaResult getProductOverviewList() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        List<Product> products = this.list(wrapper);

        List<ProductOverviewVO> overviewList = products.stream().map(product -> {
            ProductOverviewVO vo = new ProductOverviewVO();
            vo.setId(product.getId());
            vo.setTitle(product.getTitle());
            // 只取前100个字符作为描述
            if (product.getDescription() != null && product.getDescription().length() > 100) {
                vo.setBrief(product.getBrief().substring(0, 100) + "...");
            } else {
                vo.setBrief(product.getBrief());
            }

            // 获取分类名称
            if (product.getCategoryId() != null) {
                ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }

            // 获取SKU信息以计算价格范围
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getProductId, product.getId());
            List<Sku> skuList = skuMapper.selectList(skuWrapper);

            if (!skuList.isEmpty()) {
                List<BigDecimal> prices = skuList.stream()
                        .map(Sku::getPrice)
                        .sorted()
                        .collect(Collectors.toList());
                vo.setMinPrice(prices.get(0));
                vo.setMaxPrice(prices.get(prices.size() - 1));
            }

            // 获取主图
            LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
            imageWrapper.eq(ProductImage::getProductId, product.getId())
                    .eq(ProductImage::getImageType, 0); // 假设0是主图
            ProductImage mainImage = productImageMapper.selectOne(imageWrapper);
            if (mainImage != null) {
                vo.setMainImageUrl(mainImage.getUrl());
            } else if (productImageMapper.selectCount(
                    new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, product.getId())) > 0) {
                // 如果没有主图，则获取第一张图
                ProductImage firstImage = productImageMapper.selectList(
                                new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, product.getId()))
                        .get(0);
                vo.setMainImageUrl(firstImage.getUrl());
            }

            return vo;
        }).collect(Collectors.toList());

        return SaResult.ok("获取商品总览列表成功").setData(overviewList);
    }

    @Override
    public SaResult getProductsBySeller(Long userId) {
        // 查询用户所属的商户id
        QueryWrapper<Merchant> merchantWrapper = new QueryWrapper<Merchant>();
        merchantWrapper.eq("user_id", userId);
        merchantWrapper.eq("status", 2);

        List<Merchant> merchants = merchantMapper.selectList(merchantWrapper);
        if (merchants == null || merchants.size() == 0) throw new BusinessException("当前用户非商家");

        // 1. 查询商品列表
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getMerchantId, merchants.get(0).getId());
        List<Product> products = this.list(productWrapper);

        // 2. 组装 VO 列表
        List<MerchantProductsVO> voList = products.stream().map(product -> {
            MerchantProductsVO vo = new MerchantProductsVO();
            vo.setId(product.getId());
            vo.setTitle(product.getTitle());
            vo.setProduct_status(product.getProductStatus());

            // 3. 获取分类名称
            if (product.getCategoryId() != null) {
                ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
                if (category != null) {
                    vo.setCategory(category.getName());
                }
            }

            // 4. 计算库存总和
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getProductId, product.getId());
            List<Sku> skus = skuMapper.selectList(skuWrapper);
            long totalStock = skus.stream().mapToLong(Sku::getStock).sum();
            vo.setStock(totalStock);

            // 5. 获取主图 URL
            LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
            imageWrapper.eq(ProductImage::getProductId, product.getId())
                    .eq(ProductImage::getImageType, 0); // 主图
            ProductImage mainImage = productImageMapper.selectOne(imageWrapper);
            if (mainImage != null) {
                vo.setImage(mainImage.getUrl());
            }

            // 6. 查找最低价格的 SKU 并设置价格
            if (!skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(Sku::getPrice)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                vo.setPrice(minPrice);
            }

            return vo;
        }).collect(Collectors.toList());

        // 7. 返回结果
        return SaResult.ok("获取卖家商品成功").setData(voList);
    }


    @Override
    public SaResult getProductsByCategory(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, categoryId);
        List<Product> products = this.list(wrapper);

        return SaResult.ok("获取分类商品成功").setData(products);
    }

    @Override
    public SaResult listProducts() {
        List<Product> products = this.list();
        return SaResult.ok("获取商品列表成功").setData(products);
    }
}
